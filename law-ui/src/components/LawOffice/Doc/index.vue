<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'LawOfficeDoc',
  data() {
    return {
      url: 'http://doc.law-office.vip/law-office-vue'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>