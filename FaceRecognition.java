import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;

public class FaceRecognition {

    public static void main(String[] args) throws Exception {

        int height = 100; // image height
        int width = 100;  // image width
        int channels = 1; // grayscale
        int outputNum = 2; // number of classes (Authorized / Unauthorized)
        int batchSize = 16;
        int epochs = 5;
        int seed = 123;

        // Dataset folder (must have subfolders per class)
        File trainData = new File("dataset/train");

        // Load dataset
        FileSplit trainSplit = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS);
        ImageRecordReader trainReader = new ImageRecordReader(height, width, channels);
        trainReader.initialize(trainSplit);
        DataSetIterator trainIter = new org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator(trainReader, batchSize, 1, outputNum);

        ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0, 1);
        trainIter.setPreProcessor(scaler);

        // CNN Model
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam(1e-3))
                .list()
                .layer(new ConvolutionLayer.Builder(5, 5)
                        .nIn(channels).stride(1, 1).nOut(20).activation(Activation.RELU).build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, new int[]{2, 2}).build())
                .layer(new ConvolutionLayer.Builder(5, 5).stride(1, 1).nOut(50).activation(Activation.RELU).build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, new int[]{2, 2}).build())
                .layer(new DenseLayer.Builder().activation(Activation.RELU).nOut(500).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum).activation(Activation.SOFTMAX).build())
                .setInputType(InputType.convolutional(height, width, channels))
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));

        // Train CNN
        for (int i = 0; i < epochs; i++) {
            model.fit(trainIter);
        }

        System.out.println("✅ CNN Training Complete! Model Ready for Face Recognition.");
        model.save(new File("model/atm_face_model.zip"));
    }
}
