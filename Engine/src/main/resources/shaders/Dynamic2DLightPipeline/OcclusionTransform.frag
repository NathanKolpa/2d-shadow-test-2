#version 400 core

in vec2 pass_texCoord;

uniform sampler2D uni_texture;

out vec4 out_fragColor;

void main()
{
    out_fragColor = texture(uni_texture, pass_texCoord);
}