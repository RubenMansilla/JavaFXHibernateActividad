package es.nebrija.dao;

import es.nebrija.entidades.Dispositivo;

import java.util.ArrayList;
import java.util.List;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import es.nebrija.entidades.Usuario;

public class DaoDispositivoImpl implements Dao<Dispositivo> {

    Session sesion;
    Transaction transaction = null;

    @Override
    public Dispositivo grabar(Dispositivo d) {
        Dispositivo dispositivo = null;
        Integer idDispositivo;
        sesion = HibernateUtil.getSessionFactory().openSession();

        try {
            transaction = sesion.beginTransaction();
            sesion.save(d);
            transaction.commit();
        } catch (HibernateException e) {
            System.out.println("Error al grabar usuario");
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return dispositivo;
    }

    @Override
    public Dispositivo leer(Integer id) {
        Dispositivo dispositivo = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = sesion.beginTransaction();
            dispositivo = sesion.get(Dispositivo.class, id);
            transaction.commit();
        } catch (HibernateException e) {
            System.out.println("Error al leer una dispositivo");
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return dispositivo;
    }

    @Override
    public Dispositivo leer(String campo, String valor) {
        Dispositivo dispositivo = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = sesion.beginTransaction();
            // Iniciar una transacción (opcional, pero recomendado)
            List<Dispositivo> dispositivos = sesion
                    .createQuery("FROM Dispositivo WHERE modelo = :modelo", Dispositivo.class).setParameter(campo, valor)
                    .getResultList();
            // Confirmar la transacción
            transaction.commit();

            if (!dispositivos.isEmpty()) {
                dispositivo = dispositivos.get(0); // Recuperar el primer resultado (o único)
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            // Cerrar la sesión
            sesion.close();
        }
        return dispositivo;
    }

    @Override
    public Dispositivo borrar(Dispositivo dispositivo) {
        return null;
    }

    @Override
    public Dispositivo modificar(Dispositivo dispositivo) {
        return null;
    }

    @Override
    public void grabarLista(List<Dispositivo> l) {

    }
    @Override
    public List<Dispositivo> leerLista() {
        List<Dispositivo> dispositivos = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = sesion.beginTransaction();

            // Usar JOIN FETCH para cargar Marca junto con Dispositivo
            dispositivos = sesion.createQuery("FROM Dispositivo d JOIN FETCH d.marca", Dispositivo.class).getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return dispositivos;
    }


}
