package domain;
import java.util.*;

public class Utils {
    // ========== 1. COLA NORMAL ==========
    class Cola<T> {
        class Nodo {
            T dato;
            Nodo siguiente;
        }
        Nodo frente, fin;

        void encolar(T dato) {
            Nodo nuevo = new Nodo();
            nuevo.dato = dato;
            if (frente == null) {
                frente = fin = nuevo;
            } else {
                fin.siguiente = nuevo;
                fin = nuevo;
            }
        }

        T desencolar() {
            if (frente == null) return null;
            T dato = frente.dato;
            frente = frente.siguiente;
            if (frente == null) fin = null;
            return dato;
        }

        boolean estaVacia() {
            return frente == null;
        }
    }

    // ========== 2. COLA CON PRIORIDAD ==========
    class ColaPrioridad<T> {
        class NodoPrio {
            T dato;
            int prioridad;
            NodoPrio siguiente;
        }
        NodoPrio cabeza;

        void encolar(T dato, int prioridad) {
            NodoPrio nuevo = new NodoPrio();
            nuevo.dato = dato;
            nuevo.prioridad = prioridad;

            if (cabeza == null || prioridad > cabeza.prioridad) {
                nuevo.siguiente = cabeza;
                cabeza = nuevo;
            } else {
                NodoPrio actual = cabeza;
                while (actual.siguiente != null && actual.siguiente.prioridad >= prioridad) {
                    actual = actual.siguiente;
                }
                nuevo.siguiente = actual.siguiente;
                actual.siguiente = nuevo;
            }
        }

        T desencolar() {
            if (cabeza == null) return null;
            T dato = cabeza.dato;
            cabeza = cabeza.siguiente;
            return dato;
        }
    }

    // ========== 3. PILA DE HISTORIAL ==========
    class Pila<T> {
        class NodoPila {
            T dato;
            NodoPila siguiente;
        }
        NodoPila tope;

        void apilar(T dato) {
            NodoPila nuevo = new NodoPila();
            nuevo.dato = dato;
            nuevo.siguiente = tope;
            tope = nuevo;
        }

        T desapilar() {
            if (tope == null) return null;
            T dato = tope.dato;
            tope = tope.siguiente;
            return dato;
        }

        void mostrar() {
            NodoPila actual = tope;
            while (actual != null) {
                System.out.println(actual.dato);
                actual = actual.siguiente;
            }
        }
    }

    // ========== 4. LISTA DE VEHICULOS ==========
    public class ListaVehiculos {
        class NodoVehiculo {
            Vehiculo vehiculo;
            NodoVehiculo siguiente;
        }
        NodoVehiculo cabeza;

        public void agregar(Vehiculo v) {
            NodoVehiculo nuevo = new NodoVehiculo();
            nuevo.vehiculo = v;

            if (cabeza == null) {
                cabeza = nuevo;
            } else {
                NodoVehiculo actual = cabeza;
                while (actual.siguiente != null) {
                    actual = actual.siguiente;
                }
                actual.siguiente = nuevo;
            }
        }

        Vehiculo buscarDisponible(String zona) {
            NodoVehiculo actual = cabeza;
            while (actual != null) {
                if (actual.vehiculo.disponible && actual.vehiculo.zona.equals(zona)) {
                    return actual.vehiculo;
                }
                actual = actual.siguiente;
            }
            return null;
        }

        public List<Vehiculo> obtenerTodos() {
            List<Vehiculo> lista = new ArrayList<>();
            NodoVehiculo actual = cabeza;
            while (actual != null) {
                lista.add(actual.vehiculo);
                actual = actual.siguiente;
            }
            return lista;
        }
    }

    // ========== 5. GRAFO (MAPA) ==========
    public class Grafo {
        private Map<String, List<Arista>> mapa = new HashMap<>();

        public class Arista {
            public String destino;
            public int distancia;
        }

        public void agregarConexion(String origen, String destino, int distancia) {
            mapa.putIfAbsent(origen, new ArrayList<>());
            mapa.putIfAbsent(destino, new ArrayList<>());

            Arista a1 = new Arista();
            a1.destino = destino;
            a1.distancia = distancia;

            Arista a2 = new Arista();
            a2.destino = origen;
            a2.distancia = distancia;

            mapa.get(origen).add(a1);
            mapa.get(destino).add(a2);
        }

        // DIJKSTRA
        public Map<String, Integer> dijkstra(String inicio) {
            Map<String, Integer> distancias = new HashMap<>();
            PriorityQueue<NodoDist> cola = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));

            for (String nodo : mapa.keySet()) {
                distancias.put(nodo, Integer.MAX_VALUE);
            }
            distancias.put(inicio, 0);
            cola.add(new NodoDist(inicio, 0));

            while (!cola.isEmpty()) {
                NodoDist actual = cola.poll();
                if (!mapa.containsKey(actual.nodo)) continue;

                for (Arista arista : mapa.get(actual.nodo)) {
                    int nuevaDist = distancias.get(actual.nodo) + arista.distancia;
                    if (nuevaDist < distancias.get(arista.destino)) {
                        distancias.put(arista.destino, nuevaDist);
                        cola.add(new NodoDist(arista.destino, nuevaDist));
                    }
                }
            }
            return distancias;
        }

        // BFS
        List<String> bfs(String inicio) {
            List<String> resultado = new ArrayList<>();
            Queue<String> cola = new LinkedList<>();
            Set<String> visitado = new HashSet<>();

            if (!mapa.containsKey(inicio)) return resultado;

            cola.add(inicio);
            visitado.add(inicio);

            while (!cola.isEmpty()) {
                String actual = cola.poll();
                resultado.add(actual);

                for (Arista arista : mapa.get(actual)) {
                    if (!visitado.contains(arista.destino)) {
                        cola.add(arista.destino);
                        visitado.add(arista.destino);
                    }
                }
            }
            return resultado;
        }

        // DFS
        List<String> dfs(String inicio) {
            List<String> resultado = new ArrayList<>();
            Set<String> visitado = new HashSet<>();
            dfsRec(inicio, visitado, resultado);
            return resultado;
        }

        private void dfsRec(String actual, Set<String> visitado, List<String> resultado) {
            if (actual == null || visitado.contains(actual) || !mapa.containsKey(actual)) return;
            visitado.add(actual);
            resultado.add(actual);
            for (Arista arista : mapa.get(actual)) {
                dfsRec(arista.destino, visitado, resultado);
            }
        }

        public Map<String, List<Arista>> obtenerMapaAristas() {
            return mapa;
        }

        // Calcular ruta usando Dijkstra con reconstrucción de camino
        public class ResultadoRuta {
            public List<String> camino;
            public int distanciaTotal;
            public String detalleAlgoritmo;
            
            public ResultadoRuta() {
                this.camino = new ArrayList<>();
                this.distanciaTotal = 0;
                this.detalleAlgoritmo = "";
            }
        }

        public ResultadoRuta calcularRutaDijkstra(String inicio, String fin) {
            ResultadoRuta resultado = new ResultadoRuta();
            StringBuilder detalle = new StringBuilder();
            
            if (!mapa.containsKey(inicio) || !mapa.containsKey(fin)) {
                resultado.detalleAlgoritmo = "Nodo no encontrado en el mapa";
                return resultado;
            }

            detalle.append("=== ALGORITMO DIJKSTRA ===\n");
            detalle.append("Inicio: ").append(inicio).append(", Destino: ").append(fin).append("\n\n");

            Map<String, Integer> distancias = new HashMap<>();
            Map<String, String> previos = new HashMap<>();
            PriorityQueue<NodoDist> cola = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));
            Set<String> procesados = new HashSet<>();

            // Inicializar distancias
            for (String nodo : mapa.keySet()) {
                distancias.put(nodo, Integer.MAX_VALUE);
            }
            distancias.put(inicio, 0);
            cola.add(new NodoDist(inicio, 0));

            detalle.append("1. Inicialización:\n");
            detalle.append("   - Distancia[").append(inicio).append("] = 0\n");
            detalle.append("   - Todos los demás nodos = INFINITO\n\n");

            int paso = 2;
            while (!cola.isEmpty()) {
                NodoDist actual = cola.poll();
                
                if (procesados.contains(actual.nodo)) continue;
                procesados.add(actual.nodo);

                detalle.append(paso++).append(". Procesando nodo '").append(actual.nodo)
                       .append("' (distancia: ").append(actual.dist).append(")\n");

                if (actual.nodo.equals(fin)) {
                    detalle.append("   ¡Destino alcanzado!\n\n");
                    break;
                }

                if (!mapa.containsKey(actual.nodo)) continue;

                for (Arista arista : mapa.get(actual.nodo)) {
                    if (procesados.contains(arista.destino)) continue;
                    
                    int nuevaDist = distancias.get(actual.nodo) + arista.distancia;
                    int distActual = distancias.get(arista.destino);
                    
                    detalle.append("   - Vecino '").append(arista.destino)
                           .append("': distancia actual=").append(distActual == Integer.MAX_VALUE ? "INF" : distActual)
                           .append(", nueva=").append(nuevaDist);
                    
                    if (nuevaDist < distActual) {
                        distancias.put(arista.destino, nuevaDist);
                        previos.put(arista.destino, actual.nodo);
                        cola.add(new NodoDist(arista.destino, nuevaDist));
                        detalle.append(" ✓ ACTUALIZADO\n");
                    } else {
                        detalle.append(" - no mejora\n");
                    }
                }
                detalle.append("\n");
            }

            // Reconstruir camino
            detalle.append("Reconstruyendo camino óptimo:\n");
            List<String> camino = new ArrayList<>();
            String nodoActual = fin;
            
            while (nodoActual != null) {
                camino.add(0, nodoActual);
                nodoActual = previos.get(nodoActual);
            }

            if (!camino.isEmpty() && camino.get(0).equals(inicio)) {
                resultado.camino = camino;
                resultado.distanciaTotal = distancias.get(fin);
                
                detalle.append("Camino: ");
                for (int i = 0; i < camino.size(); i++) {
                    detalle.append(camino.get(i));
                    if (i < camino.size() - 1) detalle.append(" → ");
                }
                detalle.append("\nDistancia total: ").append(resultado.distanciaTotal).append("\n");
            } else {
                detalle.append("No se encontró un camino válido\n");
            }

            resultado.detalleAlgoritmo = detalle.toString();
            return resultado;
        }

        class NodoDist {
            String nodo;
            int dist;
            NodoDist(String n, int d) { nodo = n; dist = d; }
        }
    }

    // ========== 6. ARBOL DE TARIFAS ==========
    public class ArbolTarifas {
        class NodoTarifa {
            String categoria;
            double precio;
            NodoTarifa izquierda, derecha;
        }
        NodoTarifa raiz;

        public void agregar(String categoria, double precio) {
            raiz = agregarRec(raiz, categoria, precio);
        }

        private NodoTarifa agregarRec(NodoTarifa nodo, String categoria, double precio) {
            if (nodo == null) {
                nodo = new NodoTarifa();
                nodo.categoria = categoria;
                nodo.precio = precio;
                return nodo;
            }

            if (categoria.compareTo(nodo.categoria) < 0) {
                nodo.izquierda = agregarRec(nodo.izquierda, categoria, precio);
            } else if (categoria.compareTo(nodo.categoria) > 0) {
                nodo.derecha = agregarRec(nodo.derecha, categoria, precio);
            }
            return nodo;
        }

        public double buscar(String categoria) {
            return buscarRec(raiz, categoria);
        }

        private double buscarRec(NodoTarifa nodo, String categoria) {
            if (nodo == null) return 0.0;
            if (categoria.equals(nodo.categoria)) return nodo.precio;
            if (categoria.compareTo(nodo.categoria) < 0) {
                return buscarRec(nodo.izquierda, categoria);
            }
            return buscarRec(nodo.derecha, categoria);
        }
    }

    // ========== INSTANCIAS PUBLICAS ==========
    public Cola<Solicitud> colaNormal = new Cola<>();
    public ColaPrioridad<Solicitud> colaUrgente = new ColaPrioridad<>();
    public Pila<String> historial = new Pila<>();
    public ListaVehiculos vehiculos = new ListaVehiculos();
    public Grafo mapa = new Grafo();
    public ArbolTarifas tarifas = new ArbolTarifas();
    public List<Servicio> servicios = new ArrayList<>();

    // ========== ALGORITMOS ==========

    // Bubble sort
    public void ordenarBurbuja(List<Vehiculo> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).servicios < lista.get(j + 1).servicios) {
                    Vehiculo temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    // Quick sort
    public void ordenarRapido(List<Vehiculo> lista, int inicio, int fin) {
        if (inicio < fin) {
            int pivote = particionar(lista, inicio, fin);
            ordenarRapido(lista, inicio, pivote - 1);
            ordenarRapido(lista, pivote + 1, fin);
        }
    }

    private int particionar(List<Vehiculo> lista, int inicio, int fin) {
        Vehiculo pivote = lista.get(fin);
        int i = inicio - 1;

        for (int j = inicio; j < fin; j++) {
            if (lista.get(j).servicios >= pivote.servicios) {
                i++;
                Vehiculo temp = lista.get(i);
                lista.set(i, lista.get(j));
                lista.set(j, temp);
            }
        }

        Vehiculo temp = lista.get(i + 1);
        lista.set(i + 1, lista.get(fin));
        lista.set(fin, temp);

        return i + 1;
    }

    // Greedy
    public Vehiculo asignarVehiculoGreedy(String zona) {
        Vehiculo v = vehiculos.buscarDisponible(zona);
        if (v != null) return v;

        ListaVehiculos.NodoVehiculo actual = vehiculos.cabeza;
        while (actual != null) {
            if (actual.vehiculo.disponible) {
                return actual.vehiculo;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    // ========== METODOS PRINCIPALES ==========
    public void agregarSolicitud(Solicitud s) {
        if (s.esUrgente()) {
            colaUrgente.encolar(s, s.prioridad);
            historial.apilar("URGENTE: " + s.cliente + " de " + s.origen + " a " + s.destino);
        } else {
            colaNormal.encolar(s);
            historial.apilar("NORMAL: " + s.cliente + " de " + s.origen + " a " + s.destino);
        }
    }

    public Servicio procesarSiguiente() {
        Solicitud solicitud = colaUrgente.desencolar();
        if (solicitud == null) {
            solicitud = colaNormal.desencolar();
        }

        if (solicitud == null) {
            historial.apilar("No hay solicitudes pendientes");
            return null;
        }

        Vehiculo vehiculo = asignarVehiculoGreedy(solicitud.origen);
        if (vehiculo == null) {
            historial.apilar("ERROR: No hay vehiculos para " + solicitud.origen);
            return null;
        }

        // Calcular ruta detallada desde vehículo hasta cliente
        Grafo.ResultadoRuta rutaVehiculo = mapa.calcularRutaDijkstra(vehiculo.zona, solicitud.origen);
        
        // Calcular ruta detallada desde cliente hasta destino
        Grafo.ResultadoRuta rutaCliente = mapa.calcularRutaDijkstra(solicitud.origen, solicitud.destino);
        
        int distanciaTotal = rutaVehiculo.distanciaTotal + rutaCliente.distanciaTotal;

        double tarifaBase = tarifas.buscar("basica");
        if (tarifaBase == 0) tarifaBase = 10.0;
        double costo = tarifaBase * distanciaTotal;

        Servicio servicio = new Servicio(solicitud, vehiculo,
            solicitud.origen + "->" + solicitud.destino, costo);
        
        // Agregar información detallada de las rutas
        servicio.rutaVehiculoCliente = String.join(" → ", rutaVehiculo.camino);
        servicio.rutaClienteDestino = String.join(" → ", rutaCliente.camino);
        servicio.algoritmoDetalle = "=== FASE 1: Vehículo → Cliente ===\n" + 
                                   rutaVehiculo.detalleAlgoritmo + "\n\n" +
                                   "=== FASE 2: Cliente → Destino ===\n" + 
                                   rutaCliente.detalleAlgoritmo;

        servicios.add(servicio);
        historial.apilar("SERVICIO #" + servicio.id + " creado: $" + costo);

        return servicio;
    }
}
