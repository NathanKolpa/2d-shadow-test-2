#version 400 core

uniform vec4 uni_color = vec4(1, 1, 1, 1);

out vec4 out_fragColor;

void main()
{
    out_fragColor = uni_color;
}