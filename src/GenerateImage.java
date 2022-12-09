public class GenerateImage {
    int vMargin; //Margen vertical
    int hMargin; //Margen horizontal
    int singleWidth; //Tamaño barra simple
    int height; //Alto
    int width; //Ancho
    int thickWidth = 10; //Tamaño barra gruesa (Solo utilizada en el code11)
    int barcodeSize; //Tamaño del código de barras propiamente dicho (mensaje)
    String result = ""; //String final usado para generar la imagen

    public GenerateImage(int vMargin, int hMargin, int singleWidth, int height) {
        this.vMargin = vMargin;
        this.hMargin = hMargin;
        this.singleWidth = singleWidth;
        this.height = height;
    }

    public void writeString(String [] binaryBarcode, boolean Code11) {
        //Se irá escribiendo el String línea por línea hasta llegar a la altura
        for (int i = 0; i < this.height; i++) {
            //En cada línea se añade el margen horizontal a la izquierda
            this.result += "\n255\n255\n255".repeat(this.hMargin); //margen izquierdo
            this.barcodeSize = 0;
            String lastNumber = "0";
            //Por cada línea escribimos el mensaje entero analizando cada dígito: si es un 1 se escribirá tres 0
            //(para representar el negro) y si es un 0 se escribirá tres 255 (para representar el blanco)
            for (String digit : binaryBarcode){
                //Imágenes codificadas en Code 11 (tienen tamaño fino y grueso)
                if (Code11) {
                    if(lastNumber.equals(digit)){
                        //Si el dígito es igual al anterior, se escribirá una barra doble para representarlo
                        //(como anteriormente se habrá escrito una barra simple, debemos añadir la diferencia)
                        this.result += ("\n" + (digit.equals("1") ? "0\n0\n0" : "255\n255\n255")).repeat(this.thickWidth-this.singleWidth);
                        this.barcodeSize += this.thickWidth - this.singleWidth;
                    } else {
                        this.result += ("\n" + (digit.equals("1") ? "0\n0\n0" : "255\n255\n255")).repeat(this.singleWidth);
                        this.barcodeSize += this.singleWidth;
                    }
                    lastNumber = digit;
                //Imágenes codificadas en Code 93 (solo tienen tamaño fino)
                } else {
                    this.result += ("\n" + (digit.equals("1") ? "0\n0\n0" : "255\n255\n255")).repeat(this.singleWidth);
                    this.barcodeSize += this.singleWidth;
                }

            }
            //Se añade el margen horizontal a la derecha
            this.result += "\n255\n255\n255".repeat(this.hMargin); //Margen derecho
        }
        writeVerticalMarginAndMetadata();

    }

    public void writeVerticalMarginAndMetadata() {
        //La altura final vendrá dada por la altura principal más los margenes verticales
        this.height += this.vMargin*2;
        //La anchura final será el tamaño del mensaje más los margenes horizontales
        this.width = this.barcodeSize + (this.hMargin * 2);
        String metadata = "P3\n" + this.width + " " + this.height + "\n255";
        //Se calculan los margenes verticales (debido a que ahora tenemos la anchura total de la imagen)
        String verticalMargin = "";
        for (int j = 0; j < this.vMargin; j++) {
            verticalMargin += "\n255\n255\n255".repeat(this.width);
        }
        //Añadimos al resultado los metadatos, el margen vertical superior, los datos ya guardados en la variable
        //result (margenes horizontales y el propio código de barras) y por último un margen vertical inferior.
        this.result=metadata + verticalMargin + this.result + verticalMargin;
    }
}
