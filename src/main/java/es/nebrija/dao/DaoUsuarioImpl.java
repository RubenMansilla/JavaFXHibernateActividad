package es.nebrija.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import es.nebrija.entidades.Usuario;

public class DaoUsuarioImpl implements Dao<Usuario> {

    Session sesion;
    Transaction transaction = null;

    public DaoUsuarioImpl() {
    }

    @Override
    public Usuario grabar(Usuario u) {
        Usuario usuario = null;
        Integer idUsuario;
        sesion = HibernateUtil.getSessionFactory().openSession();

        try {
            transaction = sesion.beginTransaction();
            // Guardar el objeto en la base de datos
            sesion.save(u);
            transaction.commit();
        } catch (HibernateException e) {
            System.out.println("Error al grabar usuario");
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return usuario;
    }

    @Override
    public Usuario leer(Integer id) {
        Usuario usuario = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = sesion.beginTransaction();
            usuario = sesion.get(Usuario.class, id);
            transaction.commit();
        } catch (HibernateException e) {
            System.out.println("Error al leer una usuario");
            if (sesion.getTransaction() != null) {
                sesion.getTransaction().rollback();
            }
        } finally {
            sesion.close();
        }
        return usuario;
    }

    @Override
    public Usuario leer(String campo, String valor) {
        Usuario usuario = null;
        sesion = HibernateUtil.getSessionFactory().openSession();
        try {
            // Iniciar una transacción
            transaction = sesion.beginTransaction();

            List<Usuario> usuarios = sesion
                    .createQuery("FROM Usuario WHERE nombreUsuario = :nombreUsuario", Usuario.class).setParameter(campo, valor)
                    .getResultList();
            // Confirmar la transacción
            transaction.commit();

            if (!usuarios.isEmpty()) {
                usuario = usuarios.get(0); // Recuperar el primer resultado (o único)
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
        return usuario;
    }

@Override
public Usuario borrar(Usuario usuario) {
    return null;
}

@Override
public Usuario modificar(Usuario usuario) {
    return null;
}

@Override
public void grabarLista(List<Usuario> l) {

}

@Override
public List<Usuario> leerLista() {
    return List.of();
}
}
