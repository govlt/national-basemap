<script>
	import { MapLibre } from 'svelte-maplibre';

	export let styleUrl = new URL('vector/styles/bright/style.json', import.meta.env.VITE_STYLE_BASE_URL).href;
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
