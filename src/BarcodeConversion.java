public class BarcodeConversion {

    //Función que reemplaza los 1 por líneas --> '█' y los 0 por espacios --> ' '
    public static String  binaryBarcodeToBarcode (String binaryBarcode) {
        return binaryBarcode.replace("1", "█").replace("0", " ");
    }
    //Función que reemplaza las líneas --> '█' por 1 y los espacios --> ' ' por 0
    public static String barcodeToBinaryBarcode (String barcode){
        return barcode.replace("█", "1").replace(" ", "0");
    }
}


