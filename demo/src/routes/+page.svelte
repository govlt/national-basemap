<script lang="ts">
	import { AttributionControl, Control, MapLibre } from 'svelte-maplibre';
	import maplibregl from 'maplibre-gl';
	import * as pmtiles from 'pmtiles';

	let protocol = new pmtiles.Protocol();
	maplibregl.addProtocol('pmtiles', protocol.tile);

	const baseUrl = import.meta.env.VITE_STYLE_BASE_URL || 'https://basemap.startupgov.lt/';

	const styles = {
		'bright': 'vector/styles/bright/style.json',
		'bright-pmtiles': 'vector/styles/bright/style-pmtiles.json',
		'positron': 'vector/styles/positron/style.json',
		'positron-pmtiles': 'vector/styles/positron/style-pmtiles.json',
	};

	let selected: 'bright' | 'bright-pmtiles' | 'positron' | 'positron-pmtiles' = 'bright';
	$: styleUrl = new URL(styles[selected], baseUrl).href;
</script>

<MapLibre
	class="map"
	standardControls
	attributionControl={false}
	hash
	style="{ styleUrl }">
	<AttributionControl
		customAttribution={`<a href="https://github.com/govlt/national-basemap" target="_blank">GitHub</a>`}
	/>
	<Control class="flex">
		<select class="controls-select" bind:value={selected}>
			<option value="bright">Bright</option>
			<option value="bright-pmtiles">Bright (PMTiles)</option>
			<option value="positron">Positron</option>
			<option value="positron-pmtiles">Positron (PMTiles)</option>
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
