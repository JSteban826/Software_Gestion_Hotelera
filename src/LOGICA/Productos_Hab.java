package LOGICA;

public class Productos_Hab {

    private String id_prod_hab;
    private String nom_prod_hab;
    private String desc;
    private String estado;
    private int valor;

    public Productos_Hab(String id_prod_hab, String nom_prod_hab, String desc, String estado, int valor) {
        this.id_prod_hab = id_prod_hab;
        this.nom_prod_hab = nom_prod_hab;
        this.desc = desc;
        this.estado = estado;
        this.valor = valor;
    }

    public String getId_prod_hab() {
        return id_prod_hab;
    }

    public String getNom_prod_hab() {
        return nom_prod_hab;
    }

    public String getDesc() {
        return desc;
    }

    public String getEstado() {
        return estado;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return nom_prod_hab;
    }

}
