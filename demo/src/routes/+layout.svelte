<script lang="ts">
	import { page } from '$app/stores';
	import { afterNavigate } from '$app/navigation';

	let title = $page.data.title;

	afterNavigate(() => {
		title = $page.data.title;
	});

</script>

<svelte:head>
	<title>{title}</title>

	<style>
      html,
      body {
          margin: 0;
          padding: 0;
          min-height: 100vh;
          /* mobile viewport bug fix */
          min-height: -webkit-fill-available;
          font-family: ProximaNova, sans-serif;
      }

      html {
          height: -webkit-fill-available;
      }
	</style>
</svelte:head>

<slot />
