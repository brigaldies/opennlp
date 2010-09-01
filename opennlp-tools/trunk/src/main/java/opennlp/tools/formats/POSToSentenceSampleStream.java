/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.formats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSSample;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.tokenize.Detokenizer;
import opennlp.tools.util.ObjectStream;

public class POSToSentenceSampleStream implements ObjectStream<SentenceSample> {
  
  private final Detokenizer detokenizer;
  
  private final ObjectStream<POSSample> samples;
  private final int chunkSize;
  
  POSToSentenceSampleStream(Detokenizer detokenizer, ObjectStream<POSSample> samples, int chunkSize) {
    
    this.samples = samples;
    
    if (detokenizer == null)
      throw new IllegalArgumentException("detokenizer must not be null!");
    
    this.detokenizer = detokenizer;
    
    if (chunkSize < 0)
      throw new IllegalArgumentException("chunkSize must be zero or larger!");
    
    if (chunkSize > 0)
      this.chunkSize = chunkSize;
    else
      this.chunkSize = Integer.MAX_VALUE;
  }
  
  
  public SentenceSample read() throws IOException {
    
    List<String[]> sentences = new ArrayList<String[]>();
    
    POSSample posSample; 
    int chunks = 0;
    while ((posSample = samples.read()) != null && chunks < chunkSize) {
      sentences.add(posSample.getSentence());
      chunks++;
    }
    
    if (sentences.size() > 0)
     return new SentenceSample(detokenizer, sentences.toArray(new String[sentences.size()][]));
    else if (posSample != null)
      return read(); // filter out empty line
    else {
      return null; // last sample was read
    }
  }

  public void reset() throws IOException, UnsupportedOperationException {
    samples.reset();
  }

  public void close() throws IOException {
    samples.close();
  }

}