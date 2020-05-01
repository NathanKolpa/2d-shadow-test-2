package infrastructure.resource.manager;

import application.resource.manager.FileParsingException;
import application.resource.manager.ResourceLoader;
import infrastructure.Allocated;
import infrastructure.opengl.texture.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static java.lang.Math.round;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class TextureResourceLoader implements ResourceLoader
{
	@Override
	public Allocated load(InputStream inputStream) throws FileParsingException
	{
		ByteBuffer imageBuffer;
		ByteBuffer image;

		try
		{
			imageBuffer = ioResourceToByteBuffer(inputStream);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			if (!stbi_info_from_memory(imageBuffer, w, h, comp))
				throw new FileParsingException("Failed to read image information: " + stbi_failure_reason());

			System.out.println("Image width: " + w.get(0));
			System.out.println("Image height: " + h.get(0));
			System.out.println("Image components: " + comp.get(0));
			System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

			// Decode the image
			image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			if (image == null)
			{
				throw new FileParsingException("Failed to load image: " + stbi_failure_reason());
			}

			int width = w.get(0);
			int height = h.get(0);
			int components = comp.get(0);

			int textureId = createTexture(image, components, width, height);

			return new Texture(textureId, width, height);
		}
	}

	private static int createTexture(ByteBuffer image, int comp, int w, int h)
	{
		int texID = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		int format;
		if (comp == 3)
		{
			if ((w & 3) != 0)
			{
				glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
			}
			format = GL_RGB;
		}
		else
		{
			premultiplyAlpha(w, h, image);

			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

			format = GL_RGBA;
		}

		glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);

		ByteBuffer input_pixels = image;
		int input_w = w;
		int input_h = h;
		int mipmapLevel = 0;
		while (1 < input_w || 1 < input_h)
		{
			int output_w = Math.max(1, input_w >> 1);
			int output_h = Math.max(1, input_h >> 1);

			ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
			stbir_resize_uint8_generic(input_pixels, input_w, input_h, input_w * comp, output_pixels, output_w, output_h, output_w * comp, comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED, STBIR_EDGE_CLAMP, STBIR_FILTER_MITCHELL, STBIR_COLORSPACE_SRGB);

			if (mipmapLevel == 0)
			{
				stbi_image_free(image);
			}
			else
			{
				memFree(input_pixels);
			}

			glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

			input_pixels = output_pixels;
			input_w = output_w;
			input_h = output_h;
		}
		if (mipmapLevel == 0)
		{
			stbi_image_free(image);
		}
		else
		{
			memFree(input_pixels);
		}

		return texID;
	}

	private static void premultiplyAlpha(int w, int h, ByteBuffer image) {
		int stride = w * 4;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = y * stride + x * 4;

				float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
				image.put(i + 0, (byte)round(((image.get(i + 0) & 0xFF) * alpha)));
				image.put(i + 1, (byte)round(((image.get(i + 1) & 0xFF) * alpha)));
				image.put(i + 2, (byte)round(((image.get(i + 2) & 0xFF) * alpha)));
			}
		}
	}

	private static ByteBuffer ioResourceToByteBuffer(InputStream source) throws IOException
	{
		ByteBuffer buffer;

		try (ReadableByteChannel rbc = Channels.newChannel(source))
		{
			buffer = BufferUtils.createByteBuffer(8192);

			while (true)
			{
				int bytes = rbc.read(buffer);
				if (bytes == -1)
				{
					break;
				}
				if (buffer.remaining() == 0)
				{
					buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity)
	{
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
}
