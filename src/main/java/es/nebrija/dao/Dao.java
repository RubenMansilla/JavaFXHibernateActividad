package es.nebrija.dao;

import java.util.List;

public interface Dao <T> {
	
	public  T grabar(T t);
	public  T leer(Integer id);
	public  T leer(String campo, String valor);
	public  T borrar(T t);
	public  T modificar(T t);
	public void grabarLista(List <T> l);
	public List <T>leerLista();

}
