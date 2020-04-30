#version 400 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;

uniform mat4 uni_mvp;

out vec2 pass_texCoord;

void main()
{
    pass_texCoord = in_texCoord;
    gl_Position = uni_mvp * vec4(in_position, 1.0);
}