/**
 * $RCSfile: DAOService.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common.database.dao;

import net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.List;

public class DAOService<T> {

    private final Class<T> tClass;

    public DAOService(Class<T> tClass) {
        this.tClass = tClass;
    }

    public List<T> findAll() throws JDSException {
        List<T> daoList = new ArrayList<>();
        DAO dao = null;
        try {
            DAOFactory<T> factory = new DAOFactory(tClass);
            dao = factory.getDAO();
            daoList = dao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException(e);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
        return daoList;
    }


    public List<T> findByWhere(String where) throws JDSException {
        List<T> cmsInfoList = new ArrayList<>();
        DAO dao = null;
        try {
            DAOFactory factory = new DAOFactory(tClass);
            dao = factory.getDAO();
            cmsInfoList = dao.find(where);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException(e);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
        return cmsInfoList;
    }


    public List<T> find(T searchdao) throws JDSException {
        List<T> daoList = new ArrayList<>();
        DAO dao = null;
        try {
            DAOFactory factory = new DAOFactory(tClass);
            dao = factory.getDAO();
            daoList = dao.find(searchdao);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException(e);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
        return daoList;
    }


    public T getCmsInfoInfo(String infoid) throws JDSException {
        T daoBean = null;
        DAO<T> dao = null;
        try {
            DAOFactory factory = new DAOFactory(tClass);
            dao = factory.getDAO();
            if (infoid == null || infoid.equals("")) {
                daoBean = dao.createBean();
            } else {
                daoBean = dao.findByPrimaryKey(infoid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException(e);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
        return daoBean;
    }

    public T update(T cmsInfo) throws JDSException {
        DAO<T> dao = null;
        try {
            DAOFactory factory = new DAOFactory(tClass);
            dao = factory.getDAO();
            dao.update(cmsInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException(e);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
        return cmsInfo;
    }

    public Boolean delete(String infoid) throws JDSException {
        DAO<T> dao = null;
        try {
            DAOFactory factory = new DAOFactory(tClass);
            dao = factory.getDAO();
            if (infoid.indexOf(";") > -1) {
                for (String uid : infoid.split(";")) {
                    dao.delete(uid);
                }
            } else {
                dao.delete(infoid);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException(e);
        } finally {
            if (dao != null) {
                dao.close();
            }
        }
        return true;
    }
}


