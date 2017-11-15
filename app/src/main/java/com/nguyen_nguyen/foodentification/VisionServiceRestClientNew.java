package com.nguyen_nguyen.foodentification;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.contract.AnalysisInDomainResult;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.HandwritingRecognitionOperation;
import com.microsoft.projectoxford.vision.contract.HandwritingRecognitionOperationResult;
import com.microsoft.projectoxford.vision.contract.Model;
import com.microsoft.projectoxford.vision.contract.ModelResult;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;
import com.microsoft.projectoxford.vision.rest.WebServiceRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class VisionServiceRestClientNew implements VisionServiceClient {
    private static final String DEFAULT_API_ROOT = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0";
    private final String apiRoot;
    private final WebServiceRequest restCall;
    private Gson gson;

    public VisionServiceRestClientNew(String subscriptKey) {
        this(subscriptKey, DEFAULT_API_ROOT);
    }

    public VisionServiceRestClientNew(String subscriptKey, String apiRoot) {
        this.gson = new Gson();
        this.restCall = new WebServiceRequest(subscriptKey);
        this.apiRoot = apiRoot.replaceAll("/$", "");
    }

    public AnalysisResult analyzeImage(String url, String[] visualFeatures, String[] details) throws VisionServiceException {
        HashMap params = new HashMap();
        this.AppendParams(params, "visualFeatures", visualFeatures);
        this.AppendParams(params, "details", details);
        String path = this.apiRoot + "/analyze";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);
        String json = (String)this.restCall.request(uri, "POST", params, (String)null, false);
        AnalysisResult visualFeature = (AnalysisResult)this.gson.fromJson(json, AnalysisResult.class);
        return visualFeature;
    }

    public AnalysisResult analyzeImage(InputStream stream, String[] visualFeatures, String[] details) throws VisionServiceException, IOException {
        HashMap params = new HashMap();
        this.AppendParams(params, "visualFeatures", visualFeatures);
        this.AppendParams(params, "details", details);
        String path = this.apiRoot + "/analyze";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        byte[] data = IOUtils.toByteArray(stream);
        params.put("data", data);
        String json = (String)this.restCall.request(uri, "POST", params, "application/octet-stream", false);
        AnalysisResult visualFeature = (AnalysisResult)this.gson.fromJson(json, AnalysisResult.class);
        return visualFeature;
    }

    public AnalysisInDomainResult analyzeImageInDomain(String url, Model model) throws VisionServiceException {
        return this.analyzeImageInDomain(url, model.name);
    }

    public AnalysisInDomainResult analyzeImageInDomain(String url, String model) throws VisionServiceException {
        HashMap params = new HashMap();
        String path = this.apiRoot + "/models/" + model + "/analyze";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);
        String json = (String)this.restCall.request(uri, "POST", params, (String)null, false);
        AnalysisInDomainResult visualFeature = (AnalysisInDomainResult)this.gson.fromJson(json, AnalysisInDomainResult.class);
        return visualFeature;
    }

    public AnalysisInDomainResult analyzeImageInDomain(InputStream stream, Model model) throws VisionServiceException, IOException {
        return this.analyzeImageInDomain(stream, model.name);
    }

    public AnalysisInDomainResult analyzeImageInDomain(InputStream stream, String model) throws VisionServiceException, IOException {
        HashMap params = new HashMap();
        String path = this.apiRoot + "/models/" + model + "/analyze";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        byte[] data = IOUtils.toByteArray(stream);
        params.put("data", data);
        String json = (String)this.restCall.request(uri, "POST", params, "application/octet-stream", false);
        AnalysisInDomainResult visualFeature = (AnalysisInDomainResult)this.gson.fromJson(json, AnalysisInDomainResult.class);
        return visualFeature;
    }

    public AnalysisResult describe(String url, int maxCandidates) throws VisionServiceException {
        HashMap params = new HashMap();
        params.put("maxCandidates", Integer.valueOf(maxCandidates));
        String path = this.apiRoot + "/describe";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);
        String json = (String)this.restCall.request(uri, "POST", params, (String)null, false);
        AnalysisResult visualFeature = (AnalysisResult)this.gson.fromJson(json, AnalysisResult.class);
        return visualFeature;
    }

    public AnalysisResult describe(InputStream stream, int maxCandidates) throws VisionServiceException, IOException {
        HashMap params = new HashMap();
        params.put("maxCandidates", Integer.valueOf(maxCandidates));
        String path = this.apiRoot + "/describe";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        byte[] data = IOUtils.toByteArray(stream);
        params.put("data", data);
        String json = (String)this.restCall.request(uri, "POST", params, "application/octet-stream", false);
        AnalysisResult visualFeature = (AnalysisResult)this.gson.fromJson(json, AnalysisResult.class);
        return visualFeature;
    }

    public ModelResult listModels() throws VisionServiceException {
        HashMap params = new HashMap();
        String path = this.apiRoot + "/models";
        String uri = WebServiceRequest.getUrl(path, params);
        String json = (String)this.restCall.request(uri, "GET", params, (String)null, false);
        ModelResult models = (ModelResult)this.gson.fromJson(json, ModelResult.class);
        return models;
    }

    public OCR recognizeText(String url, String languageCode, boolean detectOrientation) throws VisionServiceException {
        HashMap params = new HashMap();
        params.put("language", languageCode);
        params.put("detectOrientation", Boolean.valueOf(detectOrientation));
        String path = this.apiRoot + "/ocr";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);
        String json = (String)this.restCall.request(uri, "POST", params, (String)null, false);
        OCR ocr = (OCR)this.gson.fromJson(json, OCR.class);
        return ocr;
    }

    public OCR recognizeText(InputStream stream, String languageCode, boolean detectOrientation) throws VisionServiceException, IOException {
        HashMap params = new HashMap();
        params.put("language", languageCode);
        params.put("detectOrientation", Boolean.valueOf(detectOrientation));
        String path = this.apiRoot + "/ocr";
        String uri = WebServiceRequest.getUrl(path, params);
        byte[] data = IOUtils.toByteArray(stream);
        params.put("data", data);
        String json = (String)this.restCall.request(uri, "POST", params, "application/octet-stream", false);
        OCR ocr = (OCR)this.gson.fromJson(json, OCR.class);
        return ocr;
    }

    public HandwritingRecognitionOperation createHandwritingRecognitionOperationAsync(String url) throws VisionServiceException {
        HashMap params = new HashMap();
        String path = this.apiRoot + "/RecognizeText?handwriting=true";
        String uri = WebServiceRequest.getUrl(path, params);
        params.put("url", url);
        String operationUrl = (String)this.restCall.request(uri, "POST", params, (String)null, false);
        HandwritingRecognitionOperation HandwrittenOCR = new HandwritingRecognitionOperation(operationUrl);
        return HandwrittenOCR;
    }

    public HandwritingRecognitionOperation createHandwritingRecognitionOperationAsync(InputStream stream) throws VisionServiceException, IOException {
        HashMap params = new HashMap();
        String path = this.apiRoot + "/RecognizeText?handwriting=true";
        String uri = WebServiceRequest.getUrl(path, params);
        byte[] data = IOUtils.toByteArray(stream);
        params.put("data", data);
        String operationUrl = (String)this.restCall.request(uri, "POST", params, "application/octet-stream", false);
        HandwritingRecognitionOperation HandwrittenOCR = new HandwritingRecognitionOperation(operationUrl);
        return HandwrittenOCR;
    }

    public HandwritingRecognitionOperationResult getHandwritingRecognitionOperationResultAsync(String uri) throws VisionServiceException {
        String json = (String)this.restCall.request(uri, "GET", (Map)null, (String)null, false);
        HandwritingRecognitionOperationResult HandwrittenOCR = (HandwritingRecognitionOperationResult)this.gson.fromJson(json, HandwritingRecognitionOperationResult.class);
        return HandwrittenOCR;
    }

    public byte[] getThumbnail(int width, int height, boolean smartCropping, String url) throws VisionServiceException, IOException {
        HashMap params = new HashMap();
        params.put("width", Integer.valueOf(width));
        params.put("height", Integer.valueOf(height));
        params.put("smartCropping", Boolean.valueOf(smartCropping));
        String path = this.apiRoot + "/generateThumbnail";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);
        InputStream is = (InputStream)this.restCall.request(uri, "POST", params, (String)null, true);
        byte[] image = IOUtils.toByteArray(is);
        if(is != null) {
            is.close();
        }

        return image;
    }

    public byte[] getThumbnail(int width, int height, boolean smartCropping, InputStream stream) throws VisionServiceException, IOException {
        HashMap params = new HashMap();
        params.put("width", Integer.valueOf(width));
        params.put("height", Integer.valueOf(height));
        params.put("smartCropping", Boolean.valueOf(smartCropping));
        String path = this.apiRoot + "/generateThumbnail";
        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        byte[] data = IOUtils.toByteArray(stream);
        params.put("data", data);
        InputStream is = (InputStream)this.restCall.request(uri, "POST", params, "application/octet-stream", true);
        byte[] image = IOUtils.toByteArray(is);
        if(is != null) {
            is.close();
        }

        return image;
    }

    private void AppendParams(Map<String, Object> params, String name, String[] args) {
        if(args != null && args.length > 0) {
            String features = StringUtils.join(args, ',');
            params.put(name, features);
        }

    }
}
