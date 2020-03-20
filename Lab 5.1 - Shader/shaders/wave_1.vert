// интерполируемое значение текстурных координат
varying vec3  normal, color, pos;
uniform float time;
void main(void)
{
    vec4 vertex    =  gl_Vertex;
    vertex.y       =  0.5*sin(3*vertex.x+time);
    
    pos = vec3(gl_ModelViewMatrix * vertex);
    color = gl_Color.rgb;
    vec3 vertex_normal = vec3(0.5*cos(3*vertex.x+time),-1,0);
    vertex_normal.y=vertex_normal.y<0?-vertex_normal.y:vertex_normal.y;
    normal       =  normalize(gl_NormalMatrix * vertex_normal);

    gl_Position =  gl_ModelViewProjectionMatrix *vertex;
}
