package com.jvirriel.demo.frontend.entitystatus.views;

import com.jvirriel.demo.frontend.core.views.base.ListViewUI;
import com.jvirriel.demo.frontend.core.views.containers.BaseContainerEventNavigate;
import com.jvirriel.demo.frontend.entitystatus.model.EntityStatusModel;
import com.jvirriel.demo.model.entitystatus.EntityStatus;
import com.jvirriel.ui.alertdialog.AlertDialog;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.ui.UI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jvirriel.demo.frontend.constant.GeneralConstants.DEFAULT_PAGE_NUMBER;
import static com.jvirriel.demo.frontend.constant.GeneralConstants.DEFAULT_PAGE_SIZE;
import static com.jvirriel.demo.frontend.constant.GeneralConstants.EMPTY_STRING;
import static com.jvirriel.demo.frontend.constant.GeneralConstants.LIST;
import static com.jvirriel.demo.frontend.entitystatus.constants.EntityStatusGeneralConstants.DEFAULT_VALUE;
import static com.jvirriel.demo.frontend.entitystatus.constants.EntityStatusGeneralConstants.ENTITY_STATUS_LIST_VIEW_TITLE;
import static com.jvirriel.demo.frontend.entitystatus.constants.EntityStatusServiceConstants.CODE;
import static com.jvirriel.demo.frontend.entitystatus.constants.EntityStatusServiceConstants.NAME;
import static com.jvirriel.search.ConditionParser.parseRsqlCondition;
import static com.jvirriel.ui.constants.GeneralConstants.*;
import static com.jvirriel.ui.constants.ValidationConstants.*;
import static com.vaadin.ui.Grid.SelectionMode.SINGLE;

/**
 * Implementación de la vista de listado correspondiente a la funcionalidad EntityStatus.
 */
public class EntityStatusListView extends ListViewUI<EntityStatus> {

    private EntityStatusModel model;
    private Set<Number> selectedValue;
    private List<EntityStatus> list;

    @Override
    public void setTitle(String title) {
        super.setTitle(ENTITY_STATUS_LIST_VIEW_TITLE);
    }

    /**
     * Método que desde la base construye una grid con sus propiedades básicas y solo se le agregan las propiedades
     * que serán de uso indispensable para terminar de construir el listado de acuerdo al requerimiento que se tiene.
     */
    @Override
    protected void buildGrid() {
        super.buildGrid();

        grid.setSelectionMode(SINGLE);
        grid.addSelectionListener(this::selectionListener);
        grid.addColumn(EntityStatus::getCode)
                .setId(CODE)
                .setCaption(CODE_LABEL);

        grid.addColumn(EntityStatus::getName)
                .setId(NAME)
                .setCaption(NAME_LABEL);

        grid.addColumn(EntityStatus::getDefaultValue)
                .setId(DEFAULT_VALUE)
                .setCaption(DEFAULT_VALUE_LABEL);
    }

    /**
     * Método a usar para llenar la grid con la información proveniente del model.
     */
    @Override
    protected void fillGrid() {
        super.fillGrid();

        model = new EntityStatusModel();

        String search = parseRsqlCondition(this.conditions);
        list = model.findAll(search, EMPTY_STRING, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

        grid.setItems(list);
    }

    /**
     * Método asociado por defecto a la grid desde la base como el manejador de eventos en la misma.
     *
     * @param event, evento que captura el valor en la lista.
     */
    @Override
    protected void selectionListener(SelectionEvent<EntityStatus> event) {
        super.selectionListener(event);

        Set<EntityStatus> selectedValues = event.getAllSelectedItems();
        Set<Number> list = new HashSet<>();
        selectedValues.forEach(item -> list.add(item.getId()));
        getInstance().addSelectedIds(list);
        selectedValue = list;
    }

    @Override
    protected boolean deleteAction() {
        try {
            Integer id = (Integer) selectedValue.stream().findFirst().get();

            AlertDialog.delete(UI.getCurrent(), DELETE_RECORD, DELETE_CONFIRMATION, YES_LABEL,
                    NO_LABEL, (AlertDialog.Listener) dialog -> {
                        if (dialog.isConfirmed()) {
                            model.delete(id);
                            AlertDialog.success(UI.getCurrent(), EMPTY_STRING,
                                    RECORD_DELETED, EMPTY_STRING, GO_ON,
                                    (AlertDialog.Listener) dialog2 -> {
                                    }, "84px");
                            bus.post(new BaseContainerEventNavigate(LIST));

                        }
                    }, null);
        } catch (Exception e) {
            AlertDialog.danger(UI.getCurrent(), EMPTY_STRING, NO_VALUES_SELECTED, EMPTY_STRING, OK_LABEL,
                    (AlertDialog.Listener) dialog -> {
                    }, null);
        }
        return super.deleteAction();
    }
}