package com.example.demo.service;

import com.example.demo.model.dto.TrainingData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BankNameClassifierService {

    private Instances trainingData;
    private FilteredClassifier classifier;
    private List<TrainingData> data = new ArrayList<>();

    public BankNameClassifierService() {
        getDataDefault();
        create();
    }

    private void getDataDefault() {
        data.add(new TrainingData("tcb", "TCB"));
        data.add(new TrainingData("tcb", "TCB"));
        data.add(new TrainingData("t c b", "TCB"));
        data.add(new TrainingData("techcombank", "TCB"));
        data.add(new TrainingData("tech com bank", "TCB"));
        data.add(new TrainingData("techcom", "TCB"));
        data.add(new TrainingData("ngân hàng techcombank", "TCB"));
        data.add(new TrainingData("v c b", "VCB"));
        data.add(new TrainingData("ngân hàng vcb", "VCB"));
        data.add(new TrainingData("vietcombank", "VCB"));
    }

    private ArrayList<Attribute> createAttributeInfo(ArrayList<String> classValues) {
        Attribute classAttribute = new Attribute("class", classValues);
        Attribute textAttribute = new Attribute("text", (ArrayList<String>) null);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(textAttribute);
        attributes.add(classAttribute);
        return attributes;
    }

    @SneakyThrows
    private void create() {
        ArrayList<String> classValues = data.stream()
                .map(TrainingData::getClassValue)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        classValues.add("OTHER");
        ArrayList<Attribute> attributes = createAttributeInfo(classValues);
        trainingData = new Instances("BankNames", attributes, 0);
        trainingData.setClassIndex(1);
        List<Instance> instances = data.stream()
                .map(d -> createTrainingInstance(trainingData, d.getText(), d.getClassValue()))
                .collect(Collectors.toCollection(ArrayList::new));
        trainingData.addAll(instances);
        // Tạo filter StringToWordVector
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(trainingData);
        filter.setLowerCaseTokens(true); // Chuyển về chữ thường
        filter.setTFTransform(true); // Sử dụng tần suất từ

        // Tạo và cấu hình FilteredClassifier
        classifier = new FilteredClassifier();
        classifier.setClassifier(new NaiveBayes());
        classifier.setFilter(filter);

        // Huấn luyện classifier
        classifier.buildClassifier(trainingData);
    }

    public Instance createTrainingInstance(Instances trainingData, String text, String classValue) {
        Instance instance = new DenseInstance(2);
        instance.setValue(trainingData.attribute(0), text);
        instance.setValue(trainingData.attribute(1), classValue);
        instance.setDataset(trainingData);
        return instance;
    }

    public String classify(String input) throws Exception {
        Instance instance = new DenseInstance(2);
        instance.setValue(trainingData.attribute(0), input);
        instance.setDataset(trainingData);

        double prediction = classifier.classifyInstance(instance);
        return trainingData.classAttribute().value((int) prediction);
    }

    public void trainMore(String text, String classValue) throws Exception {
        data.add(new TrainingData(text, classValue));
        create();
    }

    public void removeTrainingData(String text, String classValue) throws Exception {
        data.remove(new TrainingData(text, classValue));
        create();
    }
}
