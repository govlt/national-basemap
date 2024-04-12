<script>
	import { MapLibre } from 'svelte-maplibre';
</script>

<MapLibre
	class="map"
	standardControls
	style="https://basemap.startupgov.lt/vector/styles/bright/style.json">
</MapLibre>

<style>
    :global(.map) {
        position: absolute;
        top: 0;
        bottom: 0;
        width: 100%;
        z-index: 1;
    }
</style>
