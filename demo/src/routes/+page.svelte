<script lang="ts">
	import { Control, MapLibre } from 'svelte-maplibre';
	import maplibregl from 'maplibre-gl';
	import * as pmtiles from 'pmtiles';

	let protocol = new pmtiles.Protocol();
	maplibregl.addProtocol('pmtiles', protocol.tile);

	const baseUrl = import.meta.env.VITE_STYLE_BASE_URL || 'https://basemap.startupgov.lt/';

	const styles = {
		'bright': 'vector/styles/bright/style.json',
		'bright-pmtiles': 'vector/styles/bright/style-pmtiles.json'
	};

	let selected: 'bright' | 'bright-pmtiles' = 'bright';
	$: styleUrl = new URL(styles[selected], baseUrl).href;
</script>

<MapLibre
	class="map"
	standardControls
	hash
	style="{ styleUrl }">
	<Control class="flex">
		<select class="controls-select" bind:value={selected}>
			<option value="bright">Bright</option>
			<option value="bright-pmtiles">Bright (PMTiles)</option>
		</select>
	</Control>
</MapLibre>

<style>
    :global(.map) {
        position: absolute;
        top: 0;
        bottom: 0;
        width: 100%;
        z-index: 1;
    }

    .controls-select {
        box-sizing: border-box;
        padding: 5px 10px;
    }
</style>
