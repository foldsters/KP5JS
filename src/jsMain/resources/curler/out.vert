#version 300 es

#ifdef GL_ES
precision highp float;
#endif

in vec3 aPosition;

void main() {
    gl_Position = vec4((aPosition.xy*2.0)-1.0,1.0,1.0);
}