package ru.said.xql_crud;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import ru.idmt.documino.client.DmConfigureException;
import ru.idmt.documino.client.DocuminoClient;
import ru.idmt.documino.client.api.session.IDmSession;
import ru.idmt.documino.client.api.util.DmException;
import ru.idmt.documino.client.commons.session.DmLoginInfo;
import ru.said.model.Auto;
import ru.said.service.AutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainLayout extends HorizontalLayout {

    private Button add = new Button("Добавить");
    private Button delete = new Button("Удалить");
    private Button edit = new Button("Изменить");
    private TextField search = new TextField();
    private ListDataProvider<Auto> dataProvider;
    private List<Auto> automobilesList = new ArrayList<>();
    private Grid<Auto> grid = new Grid<>();
    private VerticalLayout verticalLayout = new VerticalLayout();
    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    private static final Logger LOGGER = Logger.getLogger(MainLayout.class);
    private Binder<Auto> binder = new Binder<>();

    public MainLayout() {
        try {
            automobilesList = AutoService.getAll();
            LOGGER.debug("SELECT * FROM ddt_auto");
            dataProvider = new ListDataProvider<>(automobilesList);
            grid.setDataProvider(dataProvider);
            grid.setWidth("525");
            add.addClickListener(clickEvent -> {
                addAuto();
            });

            delete.addClickListener(clickEvent -> {
                deleteAuto();
            });

            edit.addClickListener(clickEvent -> {
                editAuto();
            });

            horizontalLayout.addComponents(add, delete, edit, search);
            verticalLayout.addComponents(horizontalLayout, grid);
            addComponent(verticalLayout);
            grid.addColumn(Auto::getR_object_id).setCaption("Id");
            grid.addColumn(Auto::getDss_model).setCaption("Model");
            grid.addColumn(Auto::getDss_body).setCaption("Body");

        } catch (DmException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    void initData2() {
        dataProvider = new ListDataProvider<>(automobilesList);
        grid.setDataProvider(dataProvider);
    }

    void addAuto() {
        final VerticalLayout layout2 = new VerticalLayout();
        final TextField modelTipeTxt = new TextField();
        final TextField bodyTxt = new TextField();
        modelTipeTxt.setCaption("Марка");
        bodyTxt.setCaption("Модель");
        Button save = new Button("Сохранить");
        save.addClickListener(clickEvent -> {
            binder.forField(modelTipeTxt).withValidator(value -> value.length() > 0, "Поле не должно быть пустым")
                    .bind(Auto::getDss_model, Auto::setDss_model);

            binder.forField(bodyTxt)
                    .withValidator(value -> value.length() > 0, "Поле не должно быть пустым")
                    .bind(Auto::getDss_body, Auto::setDss_body);
            BinderValidationStatus<Auto> status = binder.validate();

            String xql = String.format("CREATE ddt_auto OBJECT SET dss_model = '%s' SET dss_body = '%s'", modelTipeTxt.getValue(), bodyTxt.getValue());
            try (IDmSession session = DocuminoClient.get().getDmAdminClient().newSession(new DmLoginInfo(null, null))) {
                if (!status.hasErrors()) {
                    automobilesList = AutoService.addRow(session, xql);
                    LOGGER.debug(xql);
                }
            } catch (DmException | DmConfigureException | IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            Page.getCurrent().reload();
            initData2();
        });

        layout2.addComponents(modelTipeTxt, bodyTxt, save);
        addComponent(layout2);
    }

    void deleteAuto() {
 //       try  {
            String xql = String.format("DELETE ddt_auto OBJECTS WHERE r_object_id = '%s'",
                    grid.getSelectionModel().getFirstSelectedItem().get().getR_object_id ());

                ConfirmDialog.show(UI.getCurrent(),
                        "Подтверждение",
                        "Вы уверены, что хотите удалить запись?",
                        "Да",
                        "Отмена",
                        confirmDialog -> {
                            if (confirmDialog.isConfirmed()) {
                                try (IDmSession session = DocuminoClient.get().getDmAdminClient().newSession(new DmLoginInfo(null, null))){
                                    automobilesList = AutoService.deleteRow(session, xql);
                                    Page.getCurrent().reload();
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
            LOGGER.debug(xql);
//        } catch (DmException | DmConfigureException | IOException e) {
//            LOGGER.error(e.getMessage(), e);
//        }
 //       Page.getCurrent().reload();
  //      initData2();
    }

    void editAuto() {
        Button save = new Button("Сохранить");
        final VerticalLayout layout2 = new VerticalLayout();
        final TextField modelTxt = new TextField("Марка");
        final TextField bodyTxt = new TextField("Модель");
        modelTxt.setValue(grid.getSelectedItems().iterator().next().getDss_model());
        bodyTxt.setValue(grid.getSelectedItems().iterator().next().getDss_model());
        save.addClickListener(clickEvent -> {
            String model = modelTxt.getValue();
            String body = bodyTxt.getValue();
            String xql = String.format(
                    "UPDATE ddt_auto OBJECTS SET dss_model = '%s' SET dss_body = '%s' WHERE r_object_id = '%s'",
                    model,
                    body,
                    grid.getSelectionModel().getFirstSelectedItem().get().getR_object_id());
            try (IDmSession session = DocuminoClient.get().getDmAdminClient().newSession(new DmLoginInfo(null, null))) {
                automobilesList = AutoService.editRow(session, xql);
                LOGGER.debug(xql);
            } catch (DmException | DmConfigureException | IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            Page.getCurrent().reload();
            initData2();
        });
        layout2.addComponents(modelTxt, bodyTxt, save);
        addComponent(layout2);
    }
}
