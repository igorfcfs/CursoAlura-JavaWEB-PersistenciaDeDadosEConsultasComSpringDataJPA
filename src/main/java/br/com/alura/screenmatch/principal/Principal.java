package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    
//    @Autowired
    private SerieRepository repositorio;
    
    private List<Serie> series = new ArrayList<>();
    
    public Principal(SerieRepository repositorio) {
    	this.repositorio = repositorio;
	}

	public void exibeMenu() {
    	OUTER: while(true) {
	        var menu = """
	        		
	                1 - Buscar séries
	                2 - Buscar episódios
	                3 - Listar séries buscadas
	                4 - Buscar séries por título
	                5 - Buscar séries por ator
	                6 - Buscar top 5 séries
	                7 - Buscar séries por categoria
	                8 - Buscar séries por temporadas
	                
	                0 - Sair                                 
	                """;
	
	        System.out.println(menu);
	        var opcao = leitura.nextInt();
	        leitura.nextLine();
	
	        switch (opcao) {
	            case 1:
	                buscarSerieWeb();
	                break;
	            case 2:
	                buscarEpisodioPorSerie();
	                break;
	            case 3:
	                listarSeriesBuscadas();
	                break;
	            case 4:
	            	buscarSeriePorTitulo();
	            	break;
	            case 5:
	            	buscarSeriePorAtor();
	            	break;
	            case 6:
	            	buscarTop5Series();
	            	break;
	            case 7:
	            	buscarSeriesPorCategoria();
	            	break;
	            case 8:
	            	buscarSeriesPorTemporadasEAvaliacao();
	            	break;
	            case 0:
	                System.out.println("Saindo...");
	                break OUTER;
	            default:
	                System.out.println("Opção inválida");
	        }
    	}
    }

    private void buscarSeriesPorTemporadasEAvaliacao() {
    	System.out.println("Deseja buscar séries de quantas temporadas?");
    	var temporadas = leitura.nextInt();
    	System.out.println("Deseja buscar séries de quanto de avaliação?");
    	var avaliacao = leitura.nextDouble();
		List<Serie> seriesPorTemporadas = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(temporadas, avaliacao);
		System.out.println("\nSéries de até " + temporadas + " temporadas e com avaliação de no mínimo " + avaliacao);
		seriesPorTemporadas.forEach(System.out::println);
	}

	private void buscarSeriesPorCategoria() {
    	System.out.println("Deseja buscar séries de que categoria/gênero?");
    	var nomeGenero = leitura.nextLine();
    	Categoria categoria = Categoria.fromPortugues(nomeGenero);
		List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
		System.out.println("Séries da categoria " + nomeGenero);
		seriesPorCategoria.forEach(System.out::println);
	}

	private void buscarTop5Series() {
//		List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacao(); // ordem crescente
		List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc(); // ordem descrescente
		seriesTop.forEach(s -> System.out.println(s.getTitulo() + " avaliação=" + s.getAvaliacao()));
	}

	private void buscarSeriePorAtor() {
    	System.out.println("Digite o nome para busca:");
    	var nomeAtor = leitura.nextLine();
//		List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCase(nomeAtor);
    	System.out.println("Avaliações a partir de qual valor?");
    	var avaliacao = leitura.nextDouble();
		List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		System.out.println("\nSéries em que " + nomeAtor + " trabalhou:");
		seriesEncontradas.forEach(s -> System.out.println("serie=" + s.getTitulo() + " | atores=" + s.getAtores() + " | avaliacao=" + s.getAvaliacao()));
	}

	private void buscarSeriePorTitulo() {
		System.out.println("Escolha uma série pelo título: ");
		var nomeSerie = leitura.nextLine();
		Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
	
		if (serieBuscada.isPresent()) {
			System.out.println("Dados da série: " + serieBuscada.get());
		} else {
			System.out.println("Série não encontrada!");
		}
    }

	private void listarSeriesBuscadas() {
//    	List<Serie> series = new ArrayList<>();
//    	series = dadosSeries.stream().map(d -> new Serie(d)).collect(Collectors.toList());
//		dadosSeries.forEach(System.out::println);
    	series = repositorio.findAll();
    	series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);;
	}

	private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //adosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){

    	listarSeriesBuscadas();
    	System.out.println("Escolha uma série pelo nome: ");
    	var nomeSerie = leitura.nextLine();
    	
//    	Optional<Serie> serie = series.stream().filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase())).findFirst();
    	Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
    	
    	if(serie.isPresent()) {
    		var serieEncontrada = serie.get();
	        List<DadosTemporada> temporadas = new ArrayList<>();
	        
	        for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
	            var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
	            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
	            temporadas.add(dadosTemporada);
	        }
	        temporadas.forEach(System.out::println);
	        
	        List<Episodio> episodios = temporadas.stream().flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e))).collect(Collectors.toList());
	        serieEncontrada.setEpisodios(episodios);
	        repositorio.save(serieEncontrada);
    	} else {
    		System.out.println("Série não encontrada");
    	}
    }
}