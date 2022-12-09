import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main (String [] args) throws Exception {
        String str = "Coding is fun";
        UtilTests.getHexDigest("code93_Barcode_Project.ppm",
                Code93.generateImage("Barcode Project"));
        //String str = "█ █ ████ █ ██  █  ██  █  █ █ █ ██   █ █ ██   █  █ ██  █  ███ █ ██ ██ █  █ █ ████ █";
        /*String str = "██████       ██████      █████████████████████████      █████████████      ██████       ██████                   ████████████       ██████      ██████                   █████████████            ██████       ████████████      ███████      ██████      ██████████████████████████      ██████";
        str = str.trim();
        int [] minMax = getMinAndMax(str);
        System.out.println(minMax[0] + "," + minMax[1]);
        String result = "";
        String code = "";
        for (int i = 0; i < str.length(); i++) {
            int count = 1;
                if(i < str.length()-1){
                    if(str.charAt(i)!=str.charAt(i+1)) code += "1";
                    else {
                        while(str.charAt(i)==str.charAt(i+1)){
                            count++;
                            i++;
                        }
                        code += count;
                    }
                }
        }
        for (int i = 0; i < code.length(); i=i+6) {
            result += decodeWords(code.substring(i,i+6));
        }
        int length = result.length();
        String checksum = result.substring(length-3, length-1);
        String finalResult = result.substring(0, length-3) + result.substring(length-1, length);

        String [] finalResultAr = finalResult.split("");
        String checksumC = Code93.getChecksum(finalResultAr, 20);
        String [] messageCharC = Code93.addChecksumC(finalResultAr, checksumC);
        String checksumK = Code93.getChecksum(messageCharC, 15);
        String checksumCalculated = checksumC + checksumK;

        System.out.println(checksumCalculated);
        System.out.println(result);
        System.out.println(checksum);
        System.out.println(finalResult);*/
/*

        String stringImg = UtilTests.getImageAsString("code11_3659-542.ppm");
        Image img = new Image(stringImg);
        img.fillAttributes(img.allBytes[1].contains("#"));
        img.fillPixelMatrix();
        stringImg = stringImg.replace("\n", "+");
        String [] stringSplit = stringImg.split("\\+");
        String sizes [];
        int [] intArray;
        if(stringSplit[1].contains("#")){
            sizes = stringSplit[2].split(" ");
            intArray = new int [stringSplit.length-4];
            for (int i = 4, j = 0; i < intArray.length; i++,j++) {
                intArray[i] = Integer.parseInt(stringSplit[i]);
            }
        } else {
            sizes = stringSplit[1].split(" ");
            intArray = new int [stringSplit.length-3];
            for (int i = 3, j = 0; i < intArray.length; i++,j++) {
                intArray[i] = Integer.parseInt(stringSplit[i]);
            }
        }
        int width = Integer.parseInt(sizes[0]);
        int height = Integer.parseInt(sizes[1]);
        int count = 0;
        String result = "";

        for (int i = 0; i < width*3; i++) {
            count++;
            if(count==3){
                if(intArray[i]>=(255/2)){
                    result += "0";
                } else result += "1";
                count = 0;
            }
        }
        result = BarcodeNumbersConversion.binaryBarcodeToBarcode(result);
        result = Code11.decode(result);

        if(result==null){
            decodePixelBytesRotated(width, height, intArray);
        }
*/

    }

    public static String decodePixelBytesRotated(Image img){
        String result = "";
        for (int i = img.height-1; i >= 0; i--) {
            for (int j = 0; j <= img.width-1; j++) {
                result += img.pixelMatrix[i][j];
            }
        }
        result = BarcodeConversion.binaryBarcodeToBarcode(result);
        result = Code11.decode(result);
        return result;
//        int count = 0;
//        int begin = (img.width * img.height) * 3;
//        int end = img.width * 3;
//        for (int i = begin; i > end; i -= (img.width-1)*3) {
//            count++;
//            if (count == 3) {
//                if (img.pixelBytes[i] >= (255 / 2)) {
//                    result += "0";
//                } else result += "1";
//                count = 0;
//            }
//        }


    }

    private static int [] getMinAndMax (String str) {
        int min = 1;
        int max = 1;
        int count = 1;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)==str.charAt(i+1)) min++;
            else break;
        }
        for (int i = 0; i < str.length(); i++) {
            if(i<str.length()-1){
                if(str.charAt(i)==str.charAt(i+1)){
                    count++;
                } else {
                    if(count>max) max = count;
                    if(count<min) min = count;
                    count = 1;
                }
            }

        }

        return new int[]{min, max};
    }


    private static String decodeWords(String str) {
        Map<String, String> translator = new HashMap<>();
        translator.put("131112","0");
        translator.put("111213","1");
        translator.put("111312","2");
        translator.put("111411","3");
        translator.put("121113","4");
        translator.put("121212","5");
        translator.put("121311","6");
        translator.put("111114","7");
        translator.put("131211","8");
        translator.put("141111","9");
        translator.put("211113","A");
        translator.put("211212","B");
        translator.put("211311","C");
        translator.put("221112","D");
        translator.put("221211","E");
        translator.put("231111","F");
        translator.put("112113","G");
        translator.put("112212","H");
        translator.put("112311","I");
        translator.put("122112","J");
        translator.put("132111","K");
        translator.put("111123","L");
        translator.put("111222","M");
        translator.put("111321","N");
        translator.put("121122","O");
        translator.put("131121","P");
        translator.put("212112","Q");
        translator.put("212211","R");
        translator.put("211122","S");
        translator.put("211221","T");
        translator.put("221121","U");
        translator.put("222111","V");
        translator.put("112122","W");
        translator.put("112221","X");
        translator.put("122121","Y");
        translator.put("123111","Z");
        translator.put("121131","-");
        translator.put("311112",".");
        translator.put("311211"," ");
        translator.put("321111","$");
        translator.put("112131","/");
        translator.put("113121","+");
        translator.put("211131","%");
        translator.put("111141","*");
        translator.put("121221","($)");
        translator.put("312111","(%)");
        translator.put("311121","(/)");
        translator.put("122211","(+)");
        return translator.get(str);
    }

    public static String decodePixelBytesRotated(int width, int height, int [] intArray){
        String result = "";
        int count = 0;
        int begin = (width * height) * 3;
        int end = width * 3;
        for (int i = begin; i > end; i -= (width-1)*3) {
            count++;
            if (count == 3) {
                if (intArray[i] >= (255 / 2)) {
                    result += "0";
                } else result += "1";
                count = 0;
            }
        }
        result = BarcodeConversion.binaryBarcodeToBarcode(result);
        result = Code11.decode(result);
        return result;
    }

    //------------------FUNCIONES EXTRAS--------------------------//

    /*
    public static int [] getGrayscaleImage (int [] intArray) {
        int [] intArrayGrayscale = new int [intArray.length];
        int average = 0;
        for (int i = 0; i < intArray.length; i++) {
            for (int j = 0; j < 3; j++) {
                if(i<intArray.length){
                    average += intArray[i];
                    i++;
                } else break;
            }
            average = average/3;
            i--;
            intArrayGrayscale[i] = average;
            average = 0;
        }
        int count = 0;
        for (int i = 0; i < intArrayGrayscale.length; i++) {
            count++;
            if(count==3){
                intArrayGrayscale[i-1] = intArrayGrayscale[i];
                intArrayGrayscale[i-2] = intArrayGrayscale[i];
                count = 0;
            }
        }
        return intArrayGrayscale;
    }
     */

        /*private static String removeBlankSpace (String str) {
        String newStr = "";
        boolean firstBarFound = false;
        int i = 0;
        while (i < str.length()){
            if(str.charAt(i)==' ' && firstBarFound==false){
                i++;
            } else {
                firstBarFound = true;
                newStr += str.charAt(i);
                i++;
            }
        }
    return newStr;

    }*/

    /*//Dada una posición devuelve el pixel (3 números RGB)
    public int [] getPixel(int pos){
        int [] pixel = new int[3];
        for (int i = pos, j = 0; i < 3; i++, j++) {
            pixel[j] = this.pixelBytes[i];
        }
        return pixel;
    }*/

    /*public static String generateImage(String s) {
        String [] binaryBarcode = BarcodeNumbersConversion.barcodeToBinaryBarcode(encode(s)).split("");
        //String title = "code11_" + s + ".ppm";
        int vMargin = 4; //Margen vertical
        int hMargin = 8; //Margen horizontal
        int singleWidth = 3; //Tamaño barra simple
        int thickWidth = 10; //Tamaño barra gruesa
        int height = 100;//alto
        String result = "";
        int barcodeSize = 0;
        for (int i = 0; i < height; i++) {
            result += "\n255\n255\n255".repeat(hMargin); //margen izquierdo
            String lastNumber = "0";
            barcodeSize = 0;
            for(String digit : binaryBarcode){
                //Analizamos cada digito en binario y escribimos 0 o 255 en funcion de si es o no un 1.
                if(lastNumber.equals(digit)){
                    result += ("\n" + (digit.equals("1") ? "0\n0\n0" : "255\n255\n255")).repeat(thickWidth-singleWidth);
                    barcodeSize += thickWidth - singleWidth;
                } else {
                    result += ("\n" + (digit.equals("1") ? "0\n0\n0" : "255\n255\n255")).repeat(singleWidth);
                    barcodeSize += singleWidth;
                }
                lastNumber = digit;
            }
            result += "\n255\n255\n255".repeat(hMargin); //Margen derecho
        }
        height += vMargin*2;
        int width = barcodeSize + (hMargin * 2); //ancho que depende del total de tamaño del código de barras
        String metadata = "P3\n" + width + " " + height + "\n255";
        String verticalMargin = "";
        for (int j = 0; j < vMargin; j++) {
            verticalMargin += "\n255\n255\n255".repeat(width); //margen arriba y abajo
        }
        //Añadimos al resultado los metadatos, el margen vertical superior, los datos ya guardados en la variable
        //result (margenes horizontales y el propio código de barras) y por último un margen vertical inferior.
        result=metadata + verticalMargin + result + verticalMargin;

        // writeToFile(title, result);
        return result;
    }*/



}
