package LOGICA;

public class Productos_MBar {

    private String id_prod;
    private String nom_prod;
    private int valor; 

    public Productos_MBar(String id_prod, String nom_prod, int valor) {
        this.id_prod = id_prod;
        this.nom_prod = nom_prod;
        this.valor = valor;
    }

    public String getId_prod() {
        return id_prod;
    }

    public String getNom_prod() {
        return nom_prod;
    }

    public int getValor() {
        return valor;
    }


    @Override
    public String toString() {
        return nom_prod; // Para que JComboBox muestre solo el nombre
    }
    
    
}
