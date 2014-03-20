attribute vec4 aPosition;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main() {
gl_Position = projection*view*model*aPosition;
}