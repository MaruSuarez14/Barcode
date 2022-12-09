// https://en.wikipedia.org/wiki/Code_93
import java.util.*;
public class Code93 {
    // Codifica emprant Code93
    static String encode(String str) {
        //Se añade el carácter de inicio y parada
        str = "∀" + str + "∀";
        String [] message = str.split("");
        String result = "";
        message = transformToFullASCII(message);
        //Buscamos el carácter C
        String checksumC = getChecksum(message, 20);
        String [] messageCharC = addChecksumC(message, checksumC);
        //Buscamos el carácter K
        String checksumK = getChecksum(messageCharC, 15);
        //En el resultado final guardamos el mensaje codificado (que tendrá incluido el carácter de inicio)
        for (int i = 0; i < message.length-1; i++) {
            String symbol = encodeWords(message[i]);
            result += widthsToBinaryBarcode(symbol);
        }
        //Se añade el carácter de verificación C
        result += widthsToBinaryBarcode(encodeWords(checksumC));
        //Se añade el carácter de verificación K
        result += widthsToBinaryBarcode(encodeWords(checksumK));
        //Se añade el carácter de parada
        result += widthsToBinaryBarcode(encodeWords(message[message.length-1]));
        //Se añade la barra final
        result += "█";
        return result.trim();
    }

    //Esta función modifica un array para que los caracteres ASCII que no se pueden representar directamente sean
    //transformados a la secuencia correspondiente
    static String [] transformToFullASCII (String [] message){
        List <String> messageList = new ArrayList<>();
        int pos = 0;
        //Se recorre el array con el mensaje y transformamos los caracteres de la tabla FullASCII a los caracteres
        //adecuados para poder codificarlos --> Ejemplo: a --> (+) A
        for (String e: message) {
            String encodeCharacter = encodeFullASCII(e);
            if(!encodeCharacter.equals(e)) {
                //Si el mensaje no es el mismo, guardamos en una posición del array el primer carácter, y en otra guardamos
                //la letra que hay junto a él --> Ejemplo: (/)J --> list[pos] = (/), list[pos+1] = J
                int length = encodeCharacter.length();
                String encodeCharacter1 = encodeCharacter.substring(0,length-1);
                String encodeCharacter2 = encodeCharacter.substring(length-1,length);
                messageList.add(pos, encodeCharacter1);
                pos++;
                messageList.add(pos, encodeCharacter2);
            } else {
                //Si el mensaje es el mismo se guarda tal cual en el array
                messageList.add(pos, encodeCharacter);
            }
            pos++;
        }
        //Se transforma la lista en un array estático
        return messageList.toArray(message);
    }

    //Método que transforma un String, cuyo valor representa un mensaje codificado por las anchuras de los componentes,
    //al código de barras correspondiente
    static String widthsToBinaryBarcode(String widths){
        String [] widthsSplit = widths.split("");
        String result = "";
        //Recorremos el String, si la posición actual (i) es par pondremos un 1 (barra) y si es impar pondremos un 0 (espacio)
        //Se repetirá dicho número tantas veces como el número de esa posición --> Ej: 	131112 --> 	100010100
        for (int i = 0; i < widthsSplit.length; i++) {
            result += i % 2 == 0 ? "1".repeat(Integer.parseInt(widthsSplit[i])) : "0".repeat(Integer.parseInt(widthsSplit[i]));
        }
        //Se transformará los 1 en barras y los 0 en espacios
        return BarcodeConversion.binaryBarcodeToBarcode(result);
    }

    //Método que recibe un array de String y un String y devuelve un nuevo array idéntico pero añadiendo en la
    //penúltima posición el String recibido
    static String [] addChecksumC (String []message, String checksumC ){
        //Este array contendrá el mensaje recibido por parámetro y además el checkSumC
        String [] messageCharC = new String [message.length+1];
        for (int i = 0; i < message.length-1; i++) {
            messageCharC[i] = message[i];
        }
        //Se añade el carácter C
        messageCharC[messageCharC.length-2] = checksumC;
        //Se añade el "stop" final
        messageCharC[messageCharC.length-1] = message[message.length-1];
        return messageCharC;
    }

    //Método que calcula el checksum dado un array y un rango (que determinará si el peso va hasta 15 o 20).
    static String getChecksum (String [] sSplit, int range) {
        int sum = 0;
        String result = "";
        //Se codifica los caracteres a FullASCII para poder obtener su checksum
        sSplit = transformToFullASCII(sSplit);
        int [] weight = setWeight(sSplit, range); //Array para los pesos
        int [] values = getValues(sSplit); //Array para los valores
        if(weight.length==values.length){
            //Se multiplican los valores de cada array y se suman
            for (int i = 0; i < weight.length; i++) {
                sum += weight[i] * values[i];
            }
        }
        //Se obtiene el módulo del resultado
        int value = sum % 47;
        //Se transforma el valor al carácter correspondiente
        result = valueToCharacterTable(value);

        return result;
    }

    //Método que devuelve un array con los valores correspondientes dado un array de Strings con el mensaje
    static int [] getValues (String [] text){
        int [] values = new int [text.length-2];
        for (int i = 1, j=0; j < values.length; i++, j++) {
            values[j] = characterToValueTable(text[i]);
        }
        return values;
    }

    //Método que devuelve un array con los pesos correspondientes dado un array de Strings con el mensaje y un rango
    static int [] setWeight(String [] text, int range){
        int [] weigth = new int [text.length-2];
        int n = 1;
        for (int i = weigth.length-1; i >= 0 ; i--) {
            if(n>range) n = 1;
            weigth[i] = n;
            n++;
        }
        return weigth;
    }

    //Decodifica emprant Code93
    static String decode(String str) {
        str = str.trim();
        //Se estandariza el tamaño de las barras para que vayan de 1-4
        str = standardize(str);
        //Se evalúa si el último componente es una barra o no
        boolean finalBar = str.charAt(str.length() - 1) == '█';
        //Se cuenta los tamaños de los componentes y dividimos el String resultado en un array.
        String code = countElements(str);
        String [] arr = code.split("-");
        //Se decodifica cada grupo de números para obtener el carácter correspondiente
        List<String> list = Arrays.asList(arr);
        for (int i = 0; i < list.size(); i++) {
            String decodeCharacter = decodeWords(list.get(i));
                list.set(i,decodeCharacter);
        }
        //Se guarda en un string el resultado final, quitando los elementos que corresponden al checksum
        String finalResult = storeFinalResult(list);

        int size = list.size();
        //Se guarda en un string el checksum, que estará almacenado en las dos últimas posiciones del string (sin contar el stop)
        String decodedChecksum = "";
        if(size>3) decodedChecksum = list.get(size-3) + list.get(size-2);

        //Se guarda en un string el resultado final con los caracteres de inicio y de parada
        String resultStartStop = list.get(0) + finalResult + list.get(list.size()-1);
        //Calcularemos el checksum a partir del mensaje final que está almacenado en "finalResult" para comprobar
        //que coincide con el checksum que se ha descodificado
        String [] finalResultAr = resultStartStop.split("");
        String checksumC = getChecksum(finalResultAr, 20);
        String [] messageCharC = addChecksumC(finalResultAr, checksumC);
        String checksumK = getChecksum(messageCharC,15 );

        //El checksum final es la suma del checksum C y el checksum K
        String calculatedChecksum = checksumC + checksumK;

        //Si el checksum decodificado y el checksum calculado son iguales, se devuelve el resultado final
        if(decodedChecksum.equals(calculatedChecksum) && finalBar) return finalResult;
        else return null;
    }

    //Método que dado un String con un código de barras, analiza los componentes de 6 en 6 y lo transforma a la
    //a los números correspondientes de la columna "Widths".
    private static String countElements (String str) {
        String code = "";
        int countTo6 = 0;
        //Recorremos el string y comparamos cada elemento con el siguiente, si es distinto se guardará un 1 y si es
        //igual se iniciará una cuenta para saber el número de veces que se repite
        for (int i = 0; i < str.length(); i++) {
            int count = 1;
            if(i < str.length()-1){
                //Si la cuenta llega a 6 se resetea el valor y se añade un guión al String para separar los componentes
                //de 6 en 6.
                if(countTo6==6) {
                    countTo6 = 0;
                    code += "-";
                }
                if(str.charAt(i)!=str.charAt(i+1)) code += "1";
                else {
                    while(str.charAt(i)==str.charAt(i+1)){
                        count++;
                        i++;
                    }
                    code += count;
                }
            } else {
                if(countTo6!=6) code += "1";
            }
            //Se incrementa la variable al final del bucle
            countTo6++;
        }
        return code;

    }

    //Método que guarda el mensaje final decodificado en un String y decodifica los valores de la tabla ASCII extendida
    //que pueda haber en el mensaje
    private static String storeFinalResult(List<String> list){
        String finalResult = "";
        for (int i = 1; i < list.size()-3; i++) {
            //Si el componente es una codificación de la tabla ASCII extendida, lo decodificamos haciendo uso de
            //la función decodeFullASCII
            if(list.get(i)!=null && list.get(i+1)!=null) {
                if(isFullASCII(list, i)){
                    //Se debe decodificar juntos el componente actual y el siguiente --> Ej: ∅J = "*"
                    String decodedCharacter = decodeFullASCII(list.get(i) + list.get(i+1));
                    finalResult += decodedCharacter;
                    i++;
                } else {
                    //Si el componente no pertenece a dicha tabla, se guarda tal cual
                    finalResult += list.get(i);
                }
            }
        }
        return finalResult;
    }

    //Función que comprueba si un componente de la lista y el siguiente a este, pertenecen a la codificación extendida
    private static boolean isFullASCII(List<String> list, int i){
        char c = list.get(i+1).charAt(0);
        return ((list.get(i).equals("∆") || list.get(i).equals("∅") || list.get(i).equals("∃") ||
                list.get(i).equals("∂")) && (c>='A' && c<='Z'));
    }

    //Este método devuelve el string recibido por parámetro (que contiene un mensaje codificado en código de barras)
    //con el mismo mensaje normalizado al estándar: únicamente hay 4 tamaños.
    private static String standardize(String str) {
        String strStandar = "";
        //Obtenemos el mínimo de barras juntas (o espacios)
        int min = getMin(str);
        int count = 1;
        //Recorremos el mensaje codificado
        for (int i = 0; i < str.length(); i++) {
            if(i<str.length()-1){
                //Si el componente actual y el siguiente son el mismo se sumará el número de veces que se repiten
                if(str.charAt(i)==str.charAt(i+1)){
                    count++;
                } else {
                    //Cuando los componentes ya no sean iguales, dividiremos la suma (count) con el número mínimo (min)
                    //y escribiremos el carácter que haya en el string tantas veces como el resultado de la división
                    int num = (int)Math.floor(count/min);
                    strStandar += str.charAt(i) == '█' ? "█".repeat(num) : " ".repeat(num);
                    count = 1;
                }
            } else {
                strStandar += str.charAt(i);
            }
        }
        return strStandar;
    }

    //Método que obtiene el número mínimo de barras (o espacios) juntas en un mensaje
    private static int getMin(String str) {
        int min = 100;
        int count = 1;
        for (int i = 0; i < str.length(); i++) {
            if(i<str.length()-1){
                if(str.charAt(i)==str.charAt(i+1)){
                    count++;
                } else {
                    if(count<min) min = count;
                    count = 1;
                }
            }
        }
        return min;
    }


    // Decodifica una imatge. La imatge ha d'estar en format "ppm"
    public static String decodeImage(String str) {
        Image img = new Image(str);
        int begin = 0;
        int end = img.width * 3;

        //Se descodifica la imagen desde arriba
        String result  = decode(img.decodePixelBytes(begin, end));

        begin = (img.width * (img.height/2))*3;

        //Se descodifica la imagen desde la mitad
        if (result == null) {
            result = decode(img.decodePixelBytes(begin, end));
        }
        //Se decodifica la imagen al revés
        if(result == null) {
            img.fillReversePixelBytes();
            result = decode(img.decodePixelBytes(begin, end));
        }
        //Se descodifica la imagen al revés en vertical
        if(result==null) {
            result = decode(img.decodePixelBytesVertical());
        }
        //Se decodifica la imagen no inversa en vertical
        if(result==null) {
            img.fillAttributes();
            result = decode(img.decodePixelBytesVertical());
        }
        return result;
    }

    //------------------------------------ TABLAS DE CONVERSIÓN -----------------------------------------------------//

    //Método para codificar caracteres
    static String encodeWords(String str){
        Map<String, String> translator = new HashMap<>();
        translator.put("0","131112");
        translator.put("1","111213");
        translator.put("2","111312");
        translator.put("3","111411");
        translator.put("4","121113");
        translator.put("5","121212");
        translator.put("6","121311");
        translator.put("7","111114");
        translator.put("8","131211");
        translator.put("9","141111");
        translator.put("A","211113");
        translator.put("B","211212");
        translator.put("C","211311");
        translator.put("D","221112");
        translator.put("E","221211");
        translator.put("F","231111");
        translator.put("G","112113");
        translator.put("H","112212");
        translator.put("I","112311");
        translator.put("J","122112");
        translator.put("K","132111");
        translator.put("L","111123");
        translator.put("M","111222");
        translator.put("N","111321");
        translator.put("O","121122");
        translator.put("P","131121");
        translator.put("Q","212112");
        translator.put("R","212211");
        translator.put("S","211122");
        translator.put("T","211221");
        translator.put("U","221121");
        translator.put("V","222111");
        translator.put("W","112122");
        translator.put("X","112221");
        translator.put("Y","122121");
        translator.put("Z","123111");
        translator.put("-","121131");
        translator.put(".","311112");
        translator.put(" ","311211");
        translator.put("$","321111");
        translator.put("/","112131");
        translator.put("+","113121");
        translator.put("%","211131");
        translator.put("∀","111141");
        translator.put("∂","121221");
        translator.put("∃","312111");
        translator.put("∅","311121");
        translator.put("∆","122211");
        return translator.get(str);

    }

    //Método para decodificar caracteres
    private static String decodeWords(String str) {
        return switch (str) {
            case "131112" -> "0";
            case "111213" -> "1";
            case "111312" -> "2";
            case "111411" -> "3";
            case "121113" -> "4";
            case "121212" -> "5";
            case "121311" -> "6";
            case "111114" -> "7";
            case "131211" -> "8";
            case "141111" -> "9";
            case "211113" -> "A";
            case "211212" -> "B";
            case "211311" -> "C";
            case "221112" -> "D";
            case "221211" -> "E";
            case "231111" -> "F";
            case "112113" -> "G";
            case "112212" -> "H";
            case "112311" -> "I";
            case "122112" -> "J";
            case "132111" -> "K";
            case "111123" -> "L";
            case "111222" -> "M";
            case "111321" -> "N";
            case "121122" -> "O";
            case "131121" -> "P";
            case "212112" -> "Q";
            case "212211" -> "R";
            case "211122" -> "S";
            case "211221" -> "T";
            case "221121" -> "U";
            case "222111" -> "V";
            case "112122" -> "W";
            case "112221" -> "X";
            case "122121" -> "Y";
            case "123111" -> "Z";
            case "121131" -> "-";
            case "311112" -> ".";
            case "311211" -> " ";
            case "321111" -> "$";
            case "112131" -> "/";
            case "113121" -> "+";
            case "211131" -> "%";
            case "111141" -> "∀";
            case "121221" -> "∂";
            case "312111" -> "∃";
            case "311121" -> "∅";
            case "122211" -> "∆";
            default -> null;
        };
    }

    //Tabla que asocia cada carácter con su valor correspondiente
    static int characterToValueTable(String str){
        Map<String, Integer> table = new HashMap<>();
        for (int i = 0; i <= 10; i++) {
            table.put(String.valueOf(i), i);
        }
        table.put("A", 10);
        table.put("B", 11);
        table.put("C", 12);
        table.put("D", 13);
        table.put("E", 14);
        table.put("F", 15);
        table.put("G", 16);
        table.put("H", 17);
        table.put("I", 18);
        table.put("J", 19);
        table.put("K", 20);
        table.put("L", 21);
        table.put("M", 22);
        table.put("N", 23);
        table.put("O", 24);
        table.put("P", 25);
        table.put("Q", 26);
        table.put("R", 27);
        table.put("S", 28);
        table.put("T", 29);
        table.put("U", 30);
        table.put("V", 31);
        table.put("W", 32);
        table.put("X", 33);
        table.put("Y", 34);
        table.put("Z", 35);
        table.put("-", 36);
        table.put(".", 37);
        table.put(" ", 38);
        table.put("$", 39);
        table.put("/", 40);
        table.put("+", 41);
        table.put("%", 42);
        table.put("∂", 43);
        table.put("∃", 44);
        table.put("∅", 45);
        table.put("∆", 46);
        return table.get(str);

    }

    //Tabla que asocia cada valor con su carácter correspondiente
    static String valueToCharacterTable(int n){
        Map<Integer, String> table = new HashMap<>();
        for (int i = 0; i <= 10; i++) {
            table.put(i, String.valueOf(i));
        }
        table.put(10, "A");
        table.put(11, "B");
        table.put(12, "C");
        table.put(13, "D");
        table.put(14, "E");
        table.put(15, "F");
        table.put(16, "G");
        table.put(17, "H");
        table.put(18, "I");
        table.put(19, "J");
        table.put(20, "K");
        table.put(21, "L");
        table.put(22, "M");
        table.put(23, "N");
        table.put(24, "O");
        table.put(25, "P");
        table.put(26, "Q");
        table.put(27, "R");
        table.put(28, "S");
        table.put(29, "T");
        table.put(30, "U");
        table.put(31, "V");
        table.put(32, "W");
        table.put(33, "X");
        table.put(34, "Y");
        table.put(35, "Z");
        table.put(36, "-");
        table.put(37, ".");
        table.put(38, " ");
        table.put(39, "$");
        table.put(40, "/");
        table.put(41, "+");
        table.put(42, "%");
        table.put(43, "∂");
        table.put(44, "∃");
        table.put(45, "∅");
        table.put(46, "∆");
        return table.get(n);
    }

    //Método que devuelve la codificación correspondiente de cada carácter, y si no lo encuentra devuelve el mismo que
    //recibe como parámetro
    private static String encodeFullASCII (String str) {
        return switch (str) {
            case "!" -> "∅A";
            case "\"" -> "∅B";
            case "#" -> "∅C";
            case "&" -> "∅F";
            case "'" -> "∅G";
            case "(" -> "∅H";
            case ")" -> "∅I";
            case "*" -> "∅J";
            case "," -> "∅L";
            case ":" -> "∅Z";
            case ";" -> "∃F";
            case "<" -> "∃G";
            case "=" -> "∃H";
            case ">" -> "∃I";
            case "?" -> "∃J";
            case "@" -> "∃V";
            case "a" -> "∆A";
            case "b" -> "∆B";
            case "c" -> "∆C";
            case "d" -> "∆D";
            case "e" -> "∆E";
            case "f" -> "∆F";
            case "g" -> "∆G";
            case "h" -> "∆H";
            case "i" -> "∆I";
            case "j" -> "∆J";
            case "k" -> "∆K";
            case "l" -> "∆L";
            case "m" -> "∆M";
            case "n" -> "∆N";
            case "o" -> "∆O";
            case "p" -> "∆P";
            case "q" -> "∆Q";
            case "r" -> "∆R";
            case "s" -> "∆S";
            case "t" -> "∆T";
            case "u" -> "∆U";
            case "w" -> "∆W";
            case "x" -> "∆X";
            case "y" -> "∆Y";
            case "z" -> "∆Z";
            case "[" -> "∃K";
            case "\\" -> "∃L";
            case "]" -> "∃M";
            case "^" -> "∃N";
            case "_" -> "∃O";
            case "`" -> "∃W";
            case "{" -> "∃P";
            case "|" -> "∃Q";
            case "}" -> "∃R";
            case "~" -> "∃S";

            default -> str;
        };
    }

    //Método que devuelve la descodificación correspondiente de cada carácter, y si no lo encuentra devuelve el mismo
    //que recibe como parámetro
    private static String decodeFullASCII (String str) {
        return switch (str) {
            case "∅A" -> "!";
            case "∅B" -> "\"";
            case "∅C" -> "#";
            case "∅F" -> "&";
            case "∅G" -> "'";
            case "∅H" -> "(";
            case "∅I" -> ")";
            case "∅J" -> "*";
            case "∅L" -> ",";
            case "∅Z" -> ":";
            case "∃F" -> ";";
            case "∃G" -> "<";
            case "∃H" -> "=";
            case "∃I" -> ">";
            case "∃J" -> "?";
            case "∃V" -> "@";
            case "∆A" -> "a";
            case "∆B" -> "b";
            case "∆C" -> "c";
            case "∆D" -> "d";
            case "∆E" -> "e";
            case "∆F" -> "f";
            case "∆G" -> "g";
            case "∆H" -> "h";
            case "∆I" -> "i";
            case "∆J" -> "j";
            case "∆K" -> "k";
            case "∆L" -> "l";
            case "∆M" -> "m";
            case "∆N" -> "n";
            case "∆O" -> "o";
            case "∆P" -> "p";
            case "∆Q" -> "q";
            case "∆R" -> "r";
            case "∆S" -> "s";
            case "∆T" -> "t";
            case "∆U" -> "u";
            case "∆W" -> "w";
            case "∆X" -> "x";
            case "∆Y" -> "y";
            case "∆Z" -> "z";
            case "∃K" -> "[";
            case "∃L" -> "\\";
            case "∃M" -> "]";
            case "∃N" -> "^";
            case "∃O" -> "_";
            case "∃W" -> "`";
            case "∃P" -> "{";
            case "∃Q" -> "|";
            case "∃R" -> "}";
            case "∃S" -> "~";
            default -> str;
        };
    }

    // Genera imatge a partir de barcode code93
    // Unitat barra mínima: 3 pixels
    // Alçada: 100px
    // Marges: vertical: 5px, horizontal: 15px
    public static String generateImage(String s) {
        //Convertimos el mensaje a código de barras, y las barras a 0 y 1
        String [] binaryBarcode = BarcodeConversion.barcodeToBinaryBarcode(encode(s)).split("");
        //Generamos un objeto imagen con los parámetros predeterminados que establecen el tamaño de la imagen
        GenerateImage img = new GenerateImage(5, 15, 3, 180);
        //Se genera el string necesario para generar la imagen
        img.writeString(binaryBarcode, false);
        return img.result;

    }
}
