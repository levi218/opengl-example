
varying vec3  normal, color, pos;
uniform float time;
void main(void)
{
    vec4 original    =  gl_Vertex;
    float x = original.x;
    float y = original.y;
    
    vec4 newVertex = vec4(x*cos(time),y*sin(x+time),original.z,original.w);

    pos = vec3(gl_ModelViewMatrix * newVertex);
    color = gl_Color.rgb;
    vec3 vertex_normal = gl_Normal.xyz;
    normal       =  normalize(gl_ModelViewMatrix * vec4(vertex_normal,0));

    gl_Position =  gl_ModelViewProjectionMatrix *newVertex;
}
