#version 400 core

in vec2 pass_texCoord;

out vec4 out_fragColor;

void main()
{
    out_fragColor = vec4(0.5, 0.5, 0.5, 1.0);
}