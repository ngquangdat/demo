package com.example.demo.service;

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

@Slf4j
@Service
public class BankNameClassifierService {

    private FilteredClassifier classifier;
    private Instances trainingData;
    private final ArrayList<String> classValues = new ArrayList<>();

    public BankNameClassifierService() {
        create();
    }

    private ArrayList<Attribute> createAttributeInfo() {
        classValues.add("TCB");
        classValues.add("VCB");
        classValues.add("OTHER");
        Attribute classAttribute = new Attribute("class", classValues);
        Attribute textAttribute = new Attribute("text", (ArrayList<String>) null);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(textAttribute);
        attributes.add(classAttribute);
        return attributes;
    }

    @SneakyThrows
    private void create() {
        ArrayList<Attribute> attributes = createAttributeInfo();
        trainingData = new Instances("BankNames", attributes, 0);
        trainingData.setClassIndex(1);
        addTrainingInstance();
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

    private void addTrainingInstance() {
        addTrainingInstance("tcb", "TCB");
        addTrainingInstance("t c b", "TCB");
        addTrainingInstance("techcombank", "TCB");
        addTrainingInstance("tech com bank", "TCB");
        addTrainingInstance("techcom", "TCB");
        addTrainingInstance("ngân hàng techcombank", "TCB");
        addTrainingInstance("v c b", "VCB");
        addTrainingInstance("ngân hàng vcb", "VCB");
        addTrainingInstance("vietcombank", "VCB");
    }

    public void addTrainingInstance(String text, String classValue) {
        Instance instance = new DenseInstance(2);
        instance.setValue(trainingData.attribute(0), text);
        instance.setValue(trainingData.attribute(1), classValue);
        instance.setDataset(trainingData);
        trainingData.add(instance);
    }

    public String classify(String input) throws Exception {
        Instance instance = new DenseInstance(2);
        instance.setValue(trainingData.attribute(0), input);
        instance.setDataset(trainingData);

        double prediction = classifier.classifyInstance(instance);
        return trainingData.classAttribute().value((int) prediction);
    }

    public void trainMore(String text, String classValue) throws Exception {

    }

    public void removeTrainingData(String text, String classValue) throws Exception {

    }
}
