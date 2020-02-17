package ru.said.service;

import ru.idmt.documino.client.DmConfigureException;
import ru.idmt.documino.client.DocuminoClient;
import ru.idmt.documino.client.api.session.IDmSession;
import ru.idmt.documino.client.api.util.DmException;
import ru.idmt.documino.client.commons.operation.GetBeanCollection;
//import ru.idmt.documino.client.commons.operation.GetMapCollection;
import ru.idmt.documino.client.commons.operation.GetString;
import ru.idmt.documino.client.commons.session.DmLoginInfo;
import ru.said.model.Auto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//import java.util.Map;


public class AutoService {

    private AutoService() {}

    public static List<Auto> getAll() throws DmException {
        List<Auto> autoList = new ArrayList<>();
        String xql = "SELECT * FROM ddt_auto";
        try (IDmSession session = DocuminoClient.get().getDmAdminClient().newSession(new DmLoginInfo(null, null))) {
//            Collection<Map<String, Object>> autos = new GetMapCollection(xql, new ArrayList<>()).execute(session);
//            for (Map<String, Object> autoMap : autos) {
//                Auto automobile = new Auto();
//                automobile.setR_object_id((String) autoMap.get("r_object_id"));
//                automobile.setModel((String) autoMap.get("dss_model"));
//                automobile.setDss_body ((String) autoMap.get("dss_body"));
//                System.out.println(automobile.getDss_body());
//                autoList.add(automobile);
//            }
            Collection<Auto> autoCollection = new GetBeanCollection(Auto.class, xql).execute(session);
            for (Auto auto : autoCollection){
                Auto automobile = new Auto();
                automobile.setR_object_id(auto.getR_object_id());
                automobile.setDss_model(auto.getDss_model());
                automobile.setDss_body(auto.getDss_body());
                autoList.add(automobile);
                System.out.println(automobile.getDss_model());
            }


        } catch (DmConfigureException | IOException e) {
            e.printStackTrace();
        }

        return autoList;
    }

    public static List<Auto> addRow(IDmSession session, String xql) throws DmException {

        new GetString(xql, null).execute(session);
        return getAll();
    }

    public static List<Auto> deleteRow(IDmSession session, String xql) throws DmException {
        new GetString(xql, null).execute(session);
        return getAll();
    }

    public static List<Auto> editRow(IDmSession session, String xql) throws DmException {
        new GetString(xql, null).execute(session);
        return getAll();
    }
}
