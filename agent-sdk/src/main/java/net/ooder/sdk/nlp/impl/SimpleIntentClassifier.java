package net.ooder.sdk.nlp.impl;

import net.ooder.sdk.nlp.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SimpleIntentClassifier implements IntentClassifier {
    
    private static final Logger log = LoggerFactory.getLogger(SimpleIntentClassifier.class);
    
    private final Map<String, List<String>> intentExamples = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> intentKeywords = new ConcurrentHashMap<>();
    private String currentDomain = "default";
    
    @Override
    public IntentResult classify(String text) {
        return classify(text, currentDomain);
    }
    
    @Override
    public IntentResult classify(String text, String domain) {
        if (text == null || text.isEmpty()) {
            IntentResult result = new IntentResult();
            result.setIntentId("unknown");
            result.setConfidence(0.0);
            result.setDomain(domain);
            return result;
        }
        
        String lowerText = text.toLowerCase();
        Map<String, Double> scores = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : intentExamples.entrySet()) {
            String intentId = entry.getKey();
            List<String> examples = entry.getValue();
            
            double maxSimilarity = 0;
            for (String example : examples) {
                double similarity = calculateSimilarity(lowerText, example.toLowerCase());
                maxSimilarity = Math.max(maxSimilarity, similarity);
            }
            
            Map<String, Integer> keywords = intentKeywords.get(intentId);
            if (keywords != null) {
                for (Map.Entry<String, Integer> kw : keywords.entrySet()) {
                    if (lowerText.contains(kw.getKey().toLowerCase())) {
                        maxSimilarity += 0.1 * kw.getValue();
                    }
                }
            }
            
            scores.put(intentId, Math.min(maxSimilarity, 1.0));
        }
        
        List<Map.Entry<String, Double>> sorted = scores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        IntentResult result = new IntentResult();
        result.setDomain(domain);
        
        if (!sorted.isEmpty()) {
            Map.Entry<String, Double> best = sorted.get(0);
            result.setIntentId(best.getKey());
            result.setIntentName(best.getKey());
            result.setConfidence(best.getValue());
            
            List<AlternativeIntent> alternatives = new ArrayList<>();
            for (int i = 1; i < Math.min(sorted.size(), 4); i++) {
                AlternativeIntent alt = new AlternativeIntent();
                alt.setIntentId(sorted.get(i).getKey());
                alt.setConfidence(sorted.get(i).getValue());
                alternatives.add(alt);
            }
            result.setAlternatives(alternatives);
        } else {
            result.setIntentId("unknown");
            result.setConfidence(0.0);
        }
        
        result.setSlots(new HashMap<>());
        return result;
    }
    
    private double calculateSimilarity(String text1, String text2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(text1.split("[\\s,，。！？]+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(text2.split("[\\s,，。！？]+")));
        
        if (words1.isEmpty() || words2.isEmpty()) {
            return 0;
        }
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        return (double) intersection.size() / union.size();
    }
    
    @Override
    public List<IntentResult> classifyTopN(String text, int n) {
        IntentResult mainResult = classify(text);
        
        List<IntentResult> results = new ArrayList<>();
        results.add(mainResult);
        
        if (mainResult.getAlternatives() != null) {
            for (AlternativeIntent alt : mainResult.getAlternatives()) {
                IntentResult result = new IntentResult();
                result.setIntentId(alt.getIntentId());
                result.setConfidence(alt.getConfidence());
                result.setDomain(mainResult.getDomain());
                results.add(result);
            }
        }
        
        return results.stream().limit(n).collect(Collectors.toList());
    }
    
    @Override
    public void train(List<TrainingExample> examples) {
        for (TrainingExample example : examples) {
            addIntent(example.getIntentId(), Collections.singletonList(example.getText()));
        }
        log.info("Trained with {} examples", examples.size());
    }
    
    @Override
    public void addIntent(String intentId, List<String> examples) {
        intentExamples.computeIfAbsent(intentId, k -> new CopyOnWriteArrayList<>()).addAll(examples);
        log.debug("Added {} examples for intent: {}", examples.size(), intentId);
    }
    
    @Override
    public List<String> getIntents() {
        return new ArrayList<>(intentExamples.keySet());
    }
    
    @Override
    public void setDomain(String domain) {
        this.currentDomain = domain != null ? domain : "default";
    }
    
    @Override
    public String getDomain() {
        return currentDomain;
    }
    
    public void addKeyword(String intentId, String keyword, int weight) {
        intentKeywords.computeIfAbsent(intentId, k -> new ConcurrentHashMap<>())
            .put(keyword, weight);
    }
}
