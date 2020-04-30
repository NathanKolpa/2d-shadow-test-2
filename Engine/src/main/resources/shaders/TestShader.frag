#version 400 core

out vec4 fragColor;

in vec2 pass_texCoord;

uniform sampler2D uni_texture;

void main()
{
    fragColor = texture(uni_texture, pass_texCoord);
}