package mainPackage;

public class Product {
    // Constant macro of product
    private final int DEFAULT_GRAMATURE, DEFAULT_KCAL, DEFAULT_PROTEIN, DEFAULT_CARBO, DEFAULT_FAT;
    // Macro which will be modified by user
    private int gramature, kcal, protein, carbo, fat;
    private final String name, /* Name of picture of product -> */imgName;


    private Product(String name, int kcal, int protein, int carbo, int fat, String imgName) {
        this.name = name;
        DEFAULT_GRAMATURE = 100;
        this.DEFAULT_KCAL = kcal;
        this.DEFAULT_PROTEIN = protein;
        this.DEFAULT_CARBO = carbo;
        this.DEFAULT_FAT = fat;
        this.imgName = imgName;
        setDefaultValues();
    }

    public static Product newProduct(String name, int kcal, int protein, int carbo, int fat, String imgName) {
        return new Product(name, kcal, protein, carbo, fat, imgName);
    }

    public int getDEFAULT_GRAMATURE() {
        return DEFAULT_GRAMATURE;
    }

    public int getDEFAULT_KCAL() {
        return DEFAULT_KCAL;
    }

    public int getDEFAULT_PROTEIN() {
        return DEFAULT_PROTEIN;
    }

    public int getDEFAULT_CARBO() {
        return DEFAULT_CARBO;
    }

    public int getDEFAULT_FAT() {
        return DEFAULT_FAT;
    }

    public String getName() {
        return name;
    }

    public int getGramature() {
        return gramature;
    }

    public int getKcal() {
        return kcal;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbo() {
        return carbo;
    }

    public int getFat() {
        return fat;
    }

    public String getImgName() {
        return imgName;
    }

    public void setDefaultValues() {
        this.kcal = DEFAULT_KCAL;
        this.protein = DEFAULT_PROTEIN;
        this.carbo = DEFAULT_CARBO;
        this.fat = DEFAULT_FAT;
    }

    // Modify product macro based of entered gramature
    public void setGramature(double gramature) {
        this.gramature = (int) gramature;
        gramature /= 100;
        kcal = (int) (kcal * gramature);
        protein = (int) (protein * gramature);
        carbo = (int) (carbo * gramature);
        fat = (int) (fat * gramature);
    }
}
