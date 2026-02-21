package net.ooder.common.org;

import net.ooder.common.org.service.OrgWebManager;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: spk Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang
 * @version 1.0
 */
public class CtPersonFactory {
    /**
     * @param value
     * @param loadByAccount
     * @return
     * @throws RemoteException
     */
    private static Person loadFromServer(String value, boolean loadByAccount) throws ExecutionException {
        synchronized (value) {
            OrgWebManager orgWebManager =  EsbUtil.parExpression(OrgWebManager.class);
            Person person = null;
            if (!loadByAccount) {
                person = orgWebManager.getPersonByID(value).get();
            } else {
                person = orgWebManager.getPersonByAccount(value).get();
            }

            if (person instanceof CtPerson) {
                return person;
            }

            Person ctPerson = toClient(person);
            return ctPerson;
        }

    }

    /**
     * @param wsPerson
     * @return
     */
    public static Person toClient(Person wsPerson) {
        if (wsPerson == null) {
            return null;
        }

        if (wsPerson instanceof CtPerson) {
            return wsPerson;
        }

        CtPerson person = new CtPerson(wsPerson);

        return person;
    }

    public static List<Person> toClient(List<Person> wsPersons) {

        List<Person> personList = new ArrayList<Person>();

        for (Person person : wsPersons) {
            personList.add(toClient(person));
        }
        return personList;
    }

    /**
     * @param personId
     * @return
     */
    public static Person getPerson(String personId) throws PersonNotFoundException {
        Person person = null;
        try {
            person = loadFromServer(personId, false);
        } catch (Exception e) {
            //     e.printStackTrace();

            throw new PersonNotFoundException("用户不存在");
        }
        return person;
    }

    public static Person getPerson(String personAccount, Object fake) throws PersonNotFoundException {
        Person person = null;
        try {
            person = loadFromServer(personAccount, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PersonNotFoundException("用户不存在");
        }
        return person;
    }
}
