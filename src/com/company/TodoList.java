package com.company;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class TodoList extends Application {

    private ObservableList<String> list;

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override public void start(Stage stage) throws IOException {
        URL path_to_file = getClass().getResource("save.txt");
        List<Todos> list = Files.readAllLines(new File(path_to_file.getPath()).toPath())
                .stream()
                .map(line -> {
                    String[] details = line.split(",");
                    Todos cd = new Todos();
                    cd.setName(details[0]);
                    cd.setDate(details[1]);
                    cd.setDescription(details[2]);
                    return cd;
                })
                .collect(Collectors.toList());

        ObservableList<Todos> todos = FXCollections.observableArrayList(list);
        ObservableList<String> list_view = FXCollections.observableArrayList();
        for (Todos value : list) {
            list_view.add(value.getName());
        }
        ListView<String> listView = new ListView<>(list_view);
        listView.setPrefWidth(235);
        listView.setPrefHeight(200);

        Button addButton = new Button();
        addButton.setText("Add");


        TableView<Todos> table = new TableView<>(todos);
        table.setPrefWidth(235);
        table.setPrefHeight(200);

        TableColumn<Todos, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Todos, String> t) -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).setName(t.getNewValue());
                    int row_id = t.getTableView().getSelectionModel().getSelectedIndex();
                    listView.getItems().set(row_id, t.getNewValue());
                });
        table.getColumns().add(nameColumn);

        TableColumn<Todos, String> timeColumn = new TableColumn<>("Date");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));
        timeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        timeColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Todos, String> t) -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setDate(t.getNewValue()));
        table.getColumns().add(timeColumn);

        TableColumn<Todos, String> descrColumn = new TableColumn<>("Description");
        descrColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));
        descrColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descrColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Todos, String> t) -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setDescription(t.getNewValue()));
        table.getColumns().add(descrColumn);

        TextField inputField = new TextField();

        addButton.setOnAction(e -> {
            todos.addAll(FXCollections.observableArrayList(new Todos(inputField.getText())));
            this.list.add(inputField.getText());
            inputField.setText("");
            inputField.requestFocus();
        });

        addButton.disableProperty()
                .bind(Bindings.isEmpty(inputField.textProperty()));

        Button removeButton = new Button();
        removeButton.setText("Remove");

        removeButton.setOnAction(e -> {
            if (table.getSelectionModel().getSelectedItem() != null ||
                    listView.getSelectionModel().getSelectedItem() != null){
                final int idToRemove = listView.getSelectionModel().getSelectedIndex();
                final int idToRemove1 = table.getSelectionModel().getSelectedIndex();
                if (idToRemove1 == -1) {
                    listView.getItems().remove(idToRemove);
                    table.getItems().remove(idToRemove);
                } else {
                    listView.getItems().remove(idToRemove1);
                    table.getItems().remove(idToRemove1);
                }
            }
        });
        table.setEditable(true);
        listView.setEditable(true);
        HBox entryBox = new HBox();
        entryBox.getChildren().addAll(inputField, addButton, removeButton);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(listView, table, entryBox);

        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.setTitle("todos");
        stage.show();
    }
}