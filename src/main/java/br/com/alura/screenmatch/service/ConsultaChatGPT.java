package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

//GPT API Key: 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
public class ConsultaChatGPT {
    public static String obterTraducao(String texto) {
        OpenAiService service = new OpenAiService("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");


        CompletionRequest requisicao = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();


        var resposta = service.createCompletion(requisicao);
        return resposta.getChoices().get(0).getText();
    }
}
