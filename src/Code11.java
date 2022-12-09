// Consultar taula https://en.wikipedia.org/wiki/Barcode#Linear_barcodes
// Code11: https://en.wikipedia.org/wiki/Code_11

// Generadors de codis:
//     https://barcode.tec-it.com/en/Code11
//     https://www.free-barcode-generator.net/code-11/
//     https://products.aspose.app/barcode/generate
import java.util.HashMap;
import java.util.Map;

public class Code11 {

    // Codifica un String amb Code11
    static String encode(String s) {
        //Se guarda cada carácter en una posición del array y se evalúa cada uno por separado
        String [] sSplit = s.split("");
        String result = "";
        for (String c : sSplit) {
            //Cada componente se codifica a código de barras y se guarda en result
            String symbol = encodeNumbers(c);
            result += widthsToBinaryBarcode(symbol);
            result += " ";
        }
        //Se eliminan los espacios finales
        return result.trim();
    }

    //Método para codificar los números decimales a la simbología correspondiente
    static String encodeNumbers(String number){
        Map<String, String> translator = new HashMap<>();
        translator.put("0","00001");
        translator.put("1","10001");
        translator.put("2","01001");
        translator.put("3","11000");
        translator.put("4","00101");
        translator.put("5","10100");
        translator.put("6","01100");
        translator.put("7","00011");
        translator.put("8","10010");
        translator.put("9","10000");
        translator.put("-","00100");
        translator.put("*","00110");
        return translator.get(number);
    }

    //Método que transforma un String, cuyo valor representa un mensaje codificado por las anchuras de los componentes,
    //al código de barras correspondiente
    static String widthsToBinaryBarcode(String widths){
        String [] widthsSplit = widths.split("");
        String result = "";
        //Si la variable "i" es par se trata de un espacio, si la "i" es impar se trata de una barra
        for (int i = 1; i <= widthsSplit.length; i++) {
            //Si el componente anterior al actual es 1, significa que ese componente es doble
            if(widthsSplit[i-1].equals("1")){
                result += i % 2 == 0 ? "00" : "11";
            } else {
                result += i % 2 == 0 ? "0" : "1";
            }
        }
        //Se transforman los 1 en barras y los 0 en espacios
        return BarcodeConversion.binaryBarcodeToBarcode(result);
    }

    // Decodifica amb Code11
    static String decode(String s) {
        s = s.trim();
        s = standardize(s);
        String finalResult = "";
        //Recorremos el string recibido por parámetro
        for (int i = 0; i < s.length(); i++) {
            String result = "";
            //Se trabaja con los componentes de 5 en 5
            for (int j = 0; j < 5; i++, j++) {
                if(i < s.length()){
                    if(s.charAt(i)=='█') {
                        //El texto siempre acaba en asterisco (00110)
                        if(i==s.length()-1) result += "0";
                        //Si el siguiente componente es distinto se añade un 0 (ya que es simple)
                        else if (s.charAt(i + 1) == ' ') result += "0";
                        else {
                            //Si el siguiente componente es igual se añade un 1 (ya que es doble)
                            result += "1";
                            //saltamos una posición de la i
                            i++;
                        }
                    //Se realiza el proceso contrario --> espacio y luego línea
                    } else {
                        if (s.charAt(i + 1) == '█') result += "0";
                        else {
                            result += "1";
                            i++;
                        }
                    }
                }

            }
            //Decodificamos cada resultado (que serán 5 números de 0 y 1) y lo transformamos en el número decimal correspondiente
            finalResult += decodeNumbers(result);
        }

        //Si el resultado descodificado no cumple con la siguiente expresión regular, se devolverá null
        if(!finalResult.matches("\\*[0-9-]+\\*")){
            return null;
        }
        return finalResult;
    }

    //Esta función decodifica los números representados en 0 y 1 por el número decimal correspondiente
    static String decodeNumbers(String code) {
        Map<String, String> translator = new HashMap<>();
        translator.put("00001", "0");
        translator.put("10001", "1");
        translator.put("01001", "2");
        translator.put("11000", "3");
        translator.put("00101", "4");
        translator.put("10100", "5");
        translator.put("01100", "6");
        translator.put("00011", "7");
        translator.put("10010", "8");
        translator.put("10000","9");
        translator.put("00100", "-");
        translator.put("00110", "*");
        return translator.get(code);

    }

    //Este método estandariza un código de barras para tener únicamente dos tipos de tamaños: el simple (ocupa un espacio)
    //y el doble (ocupa dos espacios)
    static String standardize(String barcode){
        String result = "";
        String code = BarcodeConversion.barcodeToBinaryBarcode(barcode).trim();
        //Buscamos el tamaño mínimo del código
        int single = getMin(code);
        //El primer carácter será uno, porque siempre empieza con una línea
        String character = "1";
        int counts = 0;
        for (int i = 0; i <= code.length(); i++) {
            if(i<code.length()){
                //Comparamos cada carácter del código con el carácter almacenado
                if(code.substring(i,i+1).equals(character)){
                    //Se suma uno a la cuenta
                    counts++;
                } else {
                    //Si la cuenta es mayor que el número mínimo, significa que el carácter es doble, lo guardamos dos veces
                    if (counts > single) result += character + character ;
                    //Si la cuenta es menor que el minimo, signifca que el carácter no es doble, lo guardamos una vez
                    else result += character;
                    //Si el carácter es un 1 se transforma en un 0 y viceversa, para poder volver a hacer la comparación
                    character = character.equals("1") ? "0" : "1";
                    //La cuenta se resetea a uno ya tendremos mínimo una coincidencia
                    counts = 1;
                }
            //Se guarda el último carácter
            } else {
                result += character;
            }
        }
        //Se transforma los unos y ceros en barras y espacios
        result = BarcodeConversion.binaryBarcodeToBarcode(result);
        return  result;
    }

    //Este método obtiene el número mínimo de barras juntas
    static int getMin (String code){
        //Para buscar el mínimo, teniendo en cuenta que todos los códigos deben empezar con una línea simple,
        //sumaremos uno a la variable single mientras encontremos un 1, y pararemos cuando sea un 0.
        int single = 0;
        for (int i = 0; i < code.length(); i++) {
            if(code.charAt(i) == '1'){
                single++;
            } else {
                break;
            }
        }
        return single;
    }

    // Decodifica una imatge. La imatge ha d'estar en format "ppm"
    public static String decodeImage(String str) {
        //Se crea un objeto imagen pasándole al constructor el String con los datos de la imagen a decodificar.
        Image img = new Image(str);
        int begin = 0;
        int end = img.width * 3;

        //Se lee la imagen desde arriba
        String result = decode(img.decodePixelBytes(begin, end));

        //Se lee la imagen desde la mitad
        begin = (img.width * (img.height/2))*3;
        if (result == null) {
            result = decode(img.decodePixelBytes(begin, end));
        }

   /*     //Se decodifica la imagen rotada
        if(result==null){
            img.fillPixelMatrix();
            result = decode(img.decodePixelBytesRotated());
        }*/
  /*      //Se decodifica la imagen rotada a la inversa
        if(result==null){
            result = decode(img.decodePixelBytesRotatedInverse());
        }*/

        //Se lee la imagen en vertical
        if(result==null){
            result = decode(img.decodePixelBytesVertical());
        }
        //Se lee la imagen al revés
        if(result == null) {
            img.fillReversePixelBytes();
            result = decode(img.decodePixelBytes(begin, end));
        }

        //Se lee la imagen al revés rotada
       /* if(result==null){
            result = decode(img.decodePixelBytesRotated());
        }*/
        //Se lee la imagen al revés en vertical
        if(result==null){
            result = decode(img.decodePixelBytesVertical());
        }

        return result;
    }

    // Genera imatge a partir de codi de barres
    // Alçada: 100px
    // Marges: vertical 4px, horizontal 8px
    public static String generateImage(String s) {
        //Se codifica el mensaje a generar y se transforman las barras y espacios en unos y ceros, respectivamente.
        String [] binaryBarcode = BarcodeConversion.barcodeToBinaryBarcode(encode(s)).split("");
        //Se crea un objeto GenerateImage, encargado de escribir el string con la información necesaria para generar
        //una imagen ppm.
        GenerateImage img = new GenerateImage(4, 8, 3, 100);
        //Se genera el string necesario para generar la imagen
        img.writeString(binaryBarcode, true);
        return img.result;
    }


}
