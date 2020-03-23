//varying vec3 color; 
varying vec3 normal, color, pos;  
void main(void)
{
    float shininess = 5000;
    vec3 lightSource = vec3(0,100,10);
    vec3 nlight = normalize(lightSource - pos);
    vec3 neye = normalize(-pos);
    vec3 nnormal = normalize(normal);
    vec3 nhalf = normalize(neye+nlight);

    float diff = max(0.0, dot(nlight,nnormal));
    float spec = diff>0.0?pow(dot(nhalf,nnormal),shininess):0.0;

    gl_FragColor=vec4(color*diff+spec,1.0);
}
