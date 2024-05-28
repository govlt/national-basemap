# Plan

## 1. Generation speed

File: `utils/LanguageUtils.java`

```java
public static Map<String, Object> getNames(String name) {
    var result = new HashMap<String, Object>();

    var nonBlankName = string(name);

    var nameTag = switch (nonBlankName) {
        case "Gedimino pr." -> "Kartografų pr.";
        case "Neris" -> "Šaltibarščių upė";
        case "Vinco Kudirkos aikštė" -> "GIS aišktė";
        default -> nonBlankName;
    };

    putIfNotEmpty(result, "name", nameTag);
    putIfNotEmpty(result, "name:latin", nameTag);

    return result;
}
```

## 2. Instant style changes

Layer: `water`
Change to color: `#FF00C5`

Layer: `waterway-name`
Change to color: `#000`

## 3. Extract area of Vilnius

```shell
pmtiles extract vector/data/output/lithuania.pmtiles vilnius.pmtiles --region vilnius.geojson
```

## 4. Deploy Map

https://protomaps.github.io/PMTiles/?url=https%3A%2F%2Fcdn.biip.lt%2Ftiles%2Fpoc%2Fvilnius%2Fvilnius.pmtiles#map=14.59/54.70182/25.2767

## 5. Offline Map

https://github.com/vycius/national-basemap-offline