package es.nebrija.dao;

import es.nebrija.entidades.Dispositivo;
import es.nebrija.entidades.Marca;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DaoMarcaImpl implements Dao<Marca> {

    Session sesion;
    Transaction transaction = null;

    @Override
    public Marca grabar(Marca m) {
        Marca marca = null;
        Integer idDispositivo;
        sesion = HibernateUtil.getSessionFactory().openSession();

        try {
            transaction = sesion.beginTransaction();
            sesion.save(m);
            transaction.commit();
        } catch (HibernateException e) {
            System.out.println("Error al grabar usuario");
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return marca;
    }

    @Override
    public Marca leer(Integer id) {
        Marca marca = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = sesion.beginTransaction();
            marca = sesion.get(Marca.class, id);
            transaction.commit();
        } catch (HibernateException e) {
            System.out.println("Error al leer una marca");
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return marca;
    }

    @Override
    public Marca leer(String campo, String valor) {
        Marca marca = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = sesion.beginTransaction();
            // Iniciar una transacción (opcional, pero recomendado)
            List<Marca> marcas = sesion
                    .createQuery("FROM Marca WHERE nombreMarca = :nombreMarca", Marca.class).setParameter(campo, valor)
                    .getResultList();
            // Confirmar la transacción
            transaction.commit();

            if (!marcas.isEmpty()) {
                marca = marcas.get(0); // Recuperar el primer resultado (o único)
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
        return marca;
    }

    @Override
    public Marca borrar(Marca marca) {
        return null;
    }

    @Override
    public Marca modificar(Marca marca) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(marca);  // Usamos merge para actualizar la entidad en la base de datos
                transaction.commit();  // Confirmamos la transacción
                return marca;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();  // Si hay un error, revertimos la transacción
                }
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    public void grabarLista(List<Marca> l) {

    }

    @Override
    public List<Marca> leerLista() {
        List<Marca> marcas = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = sesion.beginTransaction();

            // Consulta para obtener todas las marcas
            marcas = sesion.createQuery("FROM Marca", Marca.class).getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return marcas != null ? marcas : List.of();  // Asegura que no se devuelva null
    }

}
