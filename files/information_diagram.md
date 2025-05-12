classDiagram
    class StorageZone {
        +String id
        +String name
    }

    class Rack {
        +String id
        +RackType type
        +String zone_id
        +int count
    }

    class Product {
        +String id
        +String name
        +double width
        +double height
        +double depth
        +RackType size
        +String rack_id
    }

    class RackType {
        <<enum>>
        SMALL
        MEDIUM
        LARGE
    }

    StorageZone "1" --> "many" Rack : contains
    Rack "1" --> "many" Product : stores
    Product --> RackType : size
    Rack --> RackType : type
