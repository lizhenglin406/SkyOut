<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'LawOfficeGit',
  data() {
    return {
      url: 'https://gitee.com/law-office/law-office-vue'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>