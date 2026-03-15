package clases;

public class Movimiento {
    
    private int idMovimiento;
    private String tipo;
    private double monto;
    private String fecha;
    private String comentarios;
    
    public Movimiento(int idMovimiento, String tipo, double monto, String fecha, String comentarios){
        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.comentarios = comentarios;
    }
    
    //Getters
    public int getIdMovimiento(){
        return idMovimiento;
    }
    
    public String getTipo(){
        return tipo;
    }
    
    public double getMonto(){
        return monto;
    }
    
    public String getFecha(){
        return fecha;
    }
    
    public String getComentarios(){
        return comentarios;
    }
    
}
