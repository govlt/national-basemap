<script>
	import { MapLibre } from 'svelte-maplibre';

	const baseUrl = import.meta.env.VITE_STYLE_BASE_URL || 'https://basemap.startupgov.lt/';

	export let styleUrl = new URL('vector/styles/bright/style.json', baseUrl).href;
</script>

<MapLibre
	class="map"
	standardControls
	hash
	style="{ styleUrl }">
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
