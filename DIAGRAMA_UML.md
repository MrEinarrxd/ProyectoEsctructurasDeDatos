# Diagrama UML de Clases - ProyectoEstructurasDeDatos

## Estructura General

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE PRESENTACIÓN                              │
├─────────────────────────────────────────────────────────────────────────────┤
│ TranspoRouteGUI                                                              │
│ - controller: GuiController                                                 │
│ - graphPanel: GraphPanel                                                    │
│ - logArea: JTextArea                                                        │
│ + initUI(): void                                                            │
│ + crearPanelSolicitud(): JPanel                                            │
│ + crearPanelProcesar(): JPanel                                             │
│ + crearPanelBFS(): JPanel                                                  │
│ + crearPanelReportes(): JPanel                                             │
│ + crearPanelHistorial(): JPanel                                            │
│ + crearPanelPersistencia(): JPanel                                         │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │ usa
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ GraphPanel                                                                   │
│ - grafo: Utils.Grafo                                                        │
│ - posiciones: Map<String, Point>                                            │
│ + calcularPosiciones(): void                                               │
│ + paintComponent(Graphics g): void                                         │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │ usa
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE LÓGICA DE NEGOCIO                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ GuiController                                                                │
│ - requestController: RequestController                                      │
│ + getMapa(): Utils.Grafo                                                    │
│ + registrarSolicitud(...): Solicitud                                        │
│ + procesarSiguienteServicio(): Servicio                                     │
│ + obtenerNodosDisponibles(): List<String>                                   │
│ + explorarMapaBFS(String): String                                           │
│ + guardarDatos(): void                                                      │
│ + cargarDatos(): void                                                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │ usa
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ RequestController                                                            │
│ - utils: Utils                                                              │
│ - dataManager: DataManager                                                  │
│ + registrarSolicitud(...): Solicitud                                        │
│ + procesarSiguienteServicio(): Servicio                                     │
│ + getMapa(): Utils.Grafo                                                    │
│ + obtenerNodosDisponibles(): List<String>                                   │
│ + explorarMapaBFS(String): String                                           │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │ usa
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE DOMINIO                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────┐      ┌──────────────────────────┐            │
│  │      Solicitud           │      │      Vehiculo            │            │
│  ├──────────────────────────┤      ├──────────────────────────┤            │
│  │ - id: int                │      │ - id: String             │            │
│  │ - cliente: String        │      │ - zona: String           │            │
│  │ - origen: String         │      │ - disponible: boolean    │            │
│  │ - destino: String        │      │ - servicios: int         │            │
│  │ - prioridad: int         │      │ - calificacion: double   │            │
│  │ + esUrgente(): boolean   │      │ + toString(): String     │            │
│  └──────────────────────────┘      └──────────────────────────┘            │
│           ▲                                    ▲                            │
│           │ referencia                        │ referencia                 │
│           │                                   │                            │
│  ┌────────┴───────────────────────────────────┴────────┐                  │
│  │                                                     │                   │
│  │                  Servicio                          │                   │
│  │ ───────────────────────────────────────────────    │                   │
│  │ - id: int                                          │                   │
│  │ - solicitud: Solicitud ───────────────────────┘    │                   │
│  │ - vehiculo: Vehiculo ──────────────────────────┘   │                   │
│  │ - ruta: String                                     │                   │
│  │ - costo: double                                    │                   │
│  │ - rutaVehiculoCliente: String                      │                   │
│  │ - rutaClienteDestino: String                       │                   │
│  │ - algoritmoDetalle: String                         │                   │
│  └────────────────────────────────────────────────────┘                   │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────┐             │
│  │                    Utils                                 │             │
│  │ ──────────────────────────────────────────────────────── │             │
│  │ - vehiculos: VehicleList                                │             │
│  │ - solicitudes: Queue<Solicitud>                          │             │
│  │ - servicios: List<Servicio>                              │             │
│  │ - tarifas: StringList                                    │             │
│  │ - mapa: Grafo                                            │             │
│  │ + procesarSiguiente(): Servicio                          │             │
│  │ + agregarSolicitud(Solicitud): void                      │             │
│  └──────────────────────────────────────────────────────────┘             │
│           │ contiene
│           ▼
│  ┌──────────────────────────────────────────────────────────┐             │
│  │                    Grafo                                 │             │
│  │ ──────────────────────────────────────────────────────── │             │
│  │ - nodes: GraphNodeList                                   │             │
│  │ + agregarConexion(String, String, int): void             │             │
│  │ + addNode(String): void                                  │             │
│  │ + addEdge(String, String, int): void                     │             │
│  │ + getNode(String): GraphNode                             │             │
│  │ + hasNode(String): boolean                               │             │
│  │ + shortestPath(String, String): PathResult               │             │
│  │ + dijkstra(String, String): PathResult                   │             │
│  │ + bfs(String): String                                    │             │
│  │ + obtenerMapaAristas(): Map<String, List<Arista>>        │             │
│  └──────────────────────────────────────────────────────────┘             │
│           │ contiene
│           ▼
│  ┌──────────────────────────────────────────────────────────┐             │
│  │                   GraphNode                              │             │
│  │ ──────────────────────────────────────────────────────── │             │
│  │ - name: String                                           │             │
│  │ - edges: EdgeList                                        │             │
│  │ + addEdge(String, int): void                             │             │
│  │ + getNeighbors(): String[]                               │             │
│  │ + getName(): String                                      │             │
│  │ + addVehicle(Vehicle): void                              │             │
│  └──────────────────────────────────────────────────────────┘             │
│           │ contiene
│           ▼
│  ┌──────────────────────────────────────────────────────────┐             │
│  │                    Edge                                  │             │
│  │ ──────────────────────────────────────────────────────── │             │
│  │ - destino: String                                        │             │
│  │ - distancia: int                                         │             │
│  │ - peso: int                                              │             │
│  │ + getDestino(): String                                   │             │
│  │ + getDistancia(): int                                    │             │
│  └──────────────────────────────────────────────────────────┘             │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────┐             │
│  │                  PathResult                              │             │
│  │ ──────────────────────────────────────────────────────── │             │
│  │ - distancia: int                                         │             │
│  │ - ruta: StringList                                       │             │
│  │ + getDistancia(): int                                    │             │
│  │ + getRuta(): StringList                                  │             │
│  └──────────────────────────────────────────────────────────┘             │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │ usa
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE DATOS                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│ DataManager                                                                  │
│ + guardarVehiculos(List, String): void                                     │
│ + cargarVehiculos(String): List<Vehiculo>                                  │
│ + guardarSolicitudes(List, String): void                                   │
│ + guardarServicios(List, String): void                                     │
│ + guardarMapa(Grafo, String): void                                         │
│ + cargarMapa(Grafo, String): void                                          │
│ + guardarTodo(Utils): void                                                 │
│ + cargarTodo(Utils): void                                                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Colecciones Personalizadas

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      ESTRUCTURAS DE DATOS CUSTOMIZADAS                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐        │
│  │      List        │  │    StringList    │  │   VehicleList    │        │
│  │ (genérica)       │  │ (extends List)   │  │ (extends List)   │        │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘        │
│         ▲                       ▲                       ▲                   │
│         │                       │                       │                   │
│         ├───────────────────────┼───────────────────────┤                  │
│         │                       │                       │                   │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐        │
│  │    EdgeList      │  │  GraphNodeList   │  │     Queue        │        │
│  │ (extends List)   │  │ (extends List)   │  │ (extends List)   │        │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘        │
│                                                                              │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐        │
│  │     Stack        │  │   NodeHistory    │  │   NodeRequest    │        │
│  │ (extends List)   │  │ (extends List)   │  │ (extends List)   │        │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Relaciones Principales

- **Composición**: 
  - `Utils` contiene `Grafo`, `VehicleList`, `Queue<Solicitud>`, `List<Servicio>`
  - `Grafo` contiene `GraphNodeList`
  - `GraphNode` contiene `EdgeList`

- **Agregación**:
  - `Servicio` referencia `Solicitud` y `Vehiculo`
  - `RequestController` usa `Utils` y `DataManager`
  - `GuiController` usa `RequestController`

- **Dependencia**:
  - `TranspoRouteGUI` depende de `GuiController`
  - `GraphPanel` depende de `Utils.Grafo`
  - `DataManager` depende de `Utils`, `Vehiculo`, `Solicitud`, `Servicio`

## Flujo de Datos

```
Usuario
   │
   ▼
TranspoRouteGUI (Presentación)
   │
   ▼
GuiController (Controlador GUI)
   │
   ▼
RequestController (Lógica de Negocio)
   │
   ├──▶ Utils (Dominio)
   │     ├──▶ Grafo (Algoritmos de enrutamiento)
   │     ├──▶ VehicleList
   │     ├──▶ Queue<Solicitud>
   │     └──▶ List<Servicio>
   │
   └──▶ DataManager (Persistencia)
         └──▶ Archivos (vehiculos.txt, servicios.txt, mapa.txt)
```

## Patrones de Diseño Utilizados

1. **Model-View-Controller (MVC)**
   - Model: `Utils`, Domain classes
   - View: `TranspoRouteGUI`, `GraphPanel`
   - Controller: `GuiController`, `RequestController`

2. **Data Access Object (DAO)**
   - `DataManager` gestiona la persistencia

3. **Composite Pattern**
   - `List` como estructura base para colecciones especializadas

4. **Strategy Pattern**
   - Múltiples algoritmos de búsqueda: `shortestPath()`, `dijkstra()`, `bfs()`
