public class Image {
    String [] allBytes; //Array que guarda todos los datos de la imagen (comentario, tamaños..)
    int [] pixelBytes; //Array que guarda solo los datos de los pixeles de la imagen (sin comentario, tamaños..)
    String [] []  pixelMatrix;
    int width; //Anchura de la imagen
    int height; //Altura de la imagen

    Image(String str){
        //Divide el string por saltos de línea y lo guarda en el array "allBytes"
        str = str.replace("\n", "+");
        this.allBytes = str.split("\\+");
        fillAttributes();
    }

    //Llena los atributos "pixelBytes", "width" y "height"
    public void fillAttributes(){
        String [] sizes;
        //Dependiendo de si el string contiene un comentario en la segunda posición del array, sabremos la posición
        // desde la cual habrá que rellenar el array pixelBytes y la posición dónde se encuentra el tamaño de la imagen
        if(allBytes[1].contains("#")){
            sizes = this.allBytes[2].split(" ");
            fillPixelBytes(4);
        } else {
            sizes = this.allBytes[1].split(" ");
            fillPixelBytes(3);
        }
        //Se establecen los tamaños de la imagen
        this.width = Integer.parseInt(sizes[0]);
        this.height = Integer.parseInt(sizes[1]);
    }

    //Llena el array pixelBytes" a partir del array "allBytes" desde la posición recibida por parámetro
    private void fillPixelBytes(int pos) {
        this.pixelBytes = new int [this.allBytes.length-pos];
        for (int i = pos; i < this.pixelBytes.length; i++) {
            this.pixelBytes[i] = Integer.parseInt(this.allBytes[i]);
        }
    }

    //Llena el array "pixelBytes" a partir de "allBytes" pero en sentido inverso
    public  void fillReversePixelBytes(){
        for (int i = this.allBytes.length -1, j = 0; i > 4 && j < this.pixelBytes.length; i--,j++) {
            this.pixelBytes[j] = Integer.parseInt(this.allBytes[i]);
        }
    }

    ///---------------------------- Métodos para recorrer la imagen --------------------------///
    //Estos métodos recorren el array de pixelBytes dado un inicio y un final (es decir por dónde tienen que recorrer
    //la imagen) y devuelve un String en formato de código de barras para posteriormente poder ser decodificado.

    //Recorre el array de bytes en horizontal a partir de los valores recibidos por parámetro.
    public String decodePixelBytes(int begin, int end){
        String result = "";
        int count = 0;
        for (int i = begin; i < begin + end; i++) {
            count++;
            //Analizamos el valor cada tres números --> un píxel --> tres valores RGB
            if (count == 3) {
                //Si el valor es mayor que la mitad de 255, se considerará un blanco --> espacio --> 0
                if (this.pixelBytes[i] >= (255 / 2)) {
                    result += "0";
                //Si el valor es menor que la mitad de 255, se considerará un negro --> barra --> 1
                } else result += "1";
                count = 0;
            }
        }
        //Se transforma el mensaje a código de barras
        result = BarcodeConversion.binaryBarcodeToBarcode(result);
        return result;
    }

    //Recorre el array de bytes en vertical
    public String decodePixelBytesVertical(){
        String result = "";
        //La imagen se empieza a recorrer por la mitad de la anchura de la imagen
        int begin = (this.width/2)*3;
        //La imagen se acaba de leer cuando llega al inferior de ésta.
        int end =  (this.height * this.width)*3 - begin;
        int count = 0;
        //La variable i se incrementa el valor de width*3 para avanzar media línea por arriba y media por debajo
        for (int i = begin; i < end; i = i + this.width * 3) {
            count++;
            if (count == 3) {
                if (this.pixelBytes[i] >= (255 / 2)) {
                    result += "0";
                } else result += "1";
                count = 0;
            }
        }
        result = BarcodeConversion.binaryBarcodeToBarcode(result);
        return result;
    }


    //------------------------ Métodos para recorrer la imagen rotada no utilizados -------------------------//

    //Recorre el array de bytes en rotado en 45 grados.
    public String decodePixelBytesRotated(){
        String result = "";
        for (int i = this.height-1, j = 0; i >= 0 && j<= this.width-1; i--, j++) {
            result += this.pixelMatrix[i][j];
        }
        result = BarcodeConversion.binaryBarcodeToBarcode(result);
        return result;

        /*int count = 0;
        int heigth = this.pixelBytes.length-1;
        for (int i = 0; i < heigth; i++) {
            count++;
           if (count == 3) {
                if (this.pixelBytes[heigth - i] >= (255 / 2)) {
                    result += "0";
                } else result += "1";
                count = 0;
             }
        }*/



    }

    //Recorre el array de bytes rotado en 45 grados a la inversa
    public String decodePixelBytesRotatedInverse(){
        String result = "";
        int count = 0;
        int heigth = this.pixelBytes.length-1;
        for (int i = 0; i < heigth; i++) {
            count++;
            if (count == 3) {
                if (this.pixelBytes[i] >= (255 / 2)) {
                    result += "0";
                } else result += "1";
                count = 0;
            }
        }
        result = BarcodeConversion.binaryBarcodeToBarcode(result);
        return result;

    }

    //A partir del array de bytes rellena un array bidimensional que representa de manera más visual la imagen
    public void fillPixelMatrix(){
        this.pixelMatrix = new String[this.height][this.width];
        int count = 0;
        int posX = 0, posY = 0, xPath = 0;
        for (int i = 0; i < this.pixelBytes.length; i++) {
            if (xPath<this.width*3){
                count++;
                if (count == 3) {
                    this.pixelMatrix[posY] [posX] = this.pixelBytes[i] >= (255/2) ? "0" : "1";
                    count = 0;
                    posX++;
                }
                xPath++;
            } else {
                xPath = 0; posX = 0; posY++;
                i--;
            }
        }
    }

}
