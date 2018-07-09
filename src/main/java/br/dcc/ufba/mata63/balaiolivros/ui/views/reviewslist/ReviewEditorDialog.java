/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.dcc.ufba.mata63.balaiolivros.ui.views.reviewslist;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import br.dcc.ufba.mata63.balaiolivros.backend.models.CategoryModel;
import br.dcc.ufba.mata63.balaiolivros.backend.controllers.CategoryService;
import br.dcc.ufba.mata63.balaiolivros.backend.models.ReviewModel;
import br.dcc.ufba.mata63.balaiolivros.ui.common.AbstractEditorDialog;

/**
 * A dialog for editing {@link ReviewModel} objects.
 */
public class ReviewEditorDialog extends AbstractEditorDialog<ReviewModel> {

    private transient CategoryService categoryService = CategoryService
            .getInstance();

    private ComboBox<CategoryModel> categoryBox = new ComboBox<>();
    private ComboBox<String> scoreBox = new ComboBox<>();
    private DatePicker lastTasted = new DatePicker();
    private TextField beverageName = new TextField();
    private TextField timesTasted = new TextField();

    public ReviewEditorDialog(BiConsumer<ReviewModel, Operation> saveHandler,
            Consumer<ReviewModel> deleteHandler) {
        super("review", saveHandler, deleteHandler);

        createNameField();
        createCategoryBox();
        createDatePicker();
        createTimesField();
        createScoreBox();
    }

    private void createScoreBox() {
        scoreBox.setLabel("Avaliacao");
        scoreBox.setRequired(true);
        scoreBox.setAllowCustomValue(false);
        scoreBox.setItems("1", "2", "3", "4", "5");
        getFormLayout().add(scoreBox);

        getBinder().forField(scoreBox)
                .withConverter(new StringToIntegerConverter(0,
                        "The score should be a number."))
                .withValidator(new IntegerRangeValidator(
                        "The tasting count must be between 1 and 5.", 1, 5))
                .bind(ReviewModel::getScore, ReviewModel::setScore);
    }

    private void createDatePicker() {
        lastTasted.setLabel("Ultimo adicionado");
        lastTasted.setRequired(true);
        lastTasted.setMax(LocalDate.now());
        lastTasted.setMin(LocalDate.of(1, 1, 1));
        lastTasted.setValue(LocalDate.now());
        getFormLayout().add(lastTasted);

        getBinder().forField(lastTasted)
                .withValidator(Objects::nonNull,
                        "The date should be in MM/dd/yyyy format.")
                .withValidator(new DateRangeValidator(
                        "The date should be neither Before Christ nor in the future.",
                        LocalDate.of(1, 1, 1), LocalDate.now()))
                .bind(ReviewModel::getDate, ReviewModel::setDate);

    }

    private void createCategoryBox() {
        categoryBox.setLabel("Categoria");
        categoryBox.setRequired(true);
        categoryBox.setItemLabelGenerator(CategoryModel::getName);
        categoryBox.setAllowCustomValue(false);
        categoryBox.setItems(categoryService.findCategories(""));
        getFormLayout().add(categoryBox);

        getBinder().forField(categoryBox)
                .withValidator(Objects::nonNull,
                        "The category should be defined.")
                .bind(ReviewModel::getCategory, ReviewModel::setCategory);
    }

    private void createTimesField() {
        timesTasted.setLabel("Numero de exemplares");
        timesTasted.setRequired(true);
        timesTasted.setPattern("[0-9]*");
        timesTasted.setPreventInvalidInput(true);
        getFormLayout().add(timesTasted);

        getBinder().forField(timesTasted)
                .withConverter(
                        new StringToIntegerConverter(0, "Must enter a number."))
                .withValidator(new IntegerRangeValidator(
                        "The tasting count must be between 1 and 99.", 1, 99))
                .bind(ReviewModel::getCount, ReviewModel::setCount);
    }

    private void createNameField() {
        beverageName.setLabel("Titulo");
        beverageName.setRequired(true);
        getFormLayout().add(beverageName);

        getBinder().forField(beverageName)
                .withConverter(String::trim, String::trim)
                .withValidator(new StringLengthValidator(
                        "Beverage name must contain at least 3 printable characters",
                        3, null))
                .bind(ReviewModel::getName, ReviewModel::setName);
    }

    @Override
    protected void confirmDelete() {
        openConfirmationDialog("Delete review",
                "Are you sure you want to delete the review for “" + getCurrentItem().getName() + "”?", "");
    }

}
