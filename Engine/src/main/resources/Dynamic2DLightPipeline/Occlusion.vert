#version 400 core

layout (location = 0) in vec2 in_position;

uniform mat4 uni_mvp;

void main()
{
    gl_Position = uni_mvp * vec4(in_position.xy, 0.0, 1.0);
}