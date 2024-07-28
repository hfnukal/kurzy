<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { Line, Bar } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, PointElement, LineElement, Colors, Filler } from 'chart.js'
import dayjs from 'dayjs';
import dayOfYear from 'dayjs/plugin/dayOfYear';

dayjs.extend(dayOfYear)

const yearFetch = ref(`${new Date().getFullYear()}`);
const year = ref();
const data = ref([]);

// const API_URL = 'http://localhost:8080/kurzy/';
const API_URL = '/kurzy/';

const fetchData = async () => {
  const response = await fetch(`${API_URL}${yearFetch.value}`);
  const d = await response.json();
  console.log('data:', { d });
  data.value = d.filter((v: any) => v).map((v: { date: string; currency: string, rate: number; }) => ({ date: dayjs(v.date).format('D. M. YYYY'), currency: v.currency, rate: v.rate }));
  year.value = yearFetch.value;
  console.log('data:', { d, data: data.value });
};

const setYear = (y: string) => {
  yearFetch.value = y;
  fetchData();
};

// Chart.js
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, PointElement, LineElement, Colors, Filler)

const canvasRef = ref();
let gradient : CanvasGradient | null = null;

onMounted(() => {
  fetchData();

  const canvasByid = <HTMLCanvasElement> document.getElementById('myChart');
  const canvasByRef = canvasRef.value.chart;
  console.log('canvas:', { canvasByid, canvasByRef });
  const ctx = canvasByid?.getContext('2d');
  if (!ctx) {
    console.log('Canvas not found');
    return;
  }
  gradient = ctx.createLinearGradient(0, 0, 0, 450);
  gradient.addColorStop(0, 'rgba(255, 0,0, 0.5)');
  gradient.addColorStop(0.5, 'rgba(255, 0, 0, 0.25)');
  gradient.addColorStop(1, 'rgba(255, 0, 0, 0)');
});

</script>

<template>
  <div class="chart">
    <h1 class="green">{{ year }}</h1>
    <div>
      <button @click="setYear('2024')">2024</button>
      <button @click="setYear('2023')">2023</button>
      <button @click="setYear('2022')">2022</button>
      <button @click="setYear('2021')">2021</button>
      <button @click="setYear('2020')">2020</button>
      
      <div style="background-color: transparent;">
        <Line
          id="myChart"
          ref="canvasRef"
          :data="{
            labels: new Array(366).fill('').map((_, i) => dayjs(`01-01-${year}`).dayOfYear(i + 1).format('D. M. YYYY')),
            datasets: [{
              label: '1 USD',
              data: data,
              fill: true,
            
              backgroundColor: (ctx) => {
                const canvas = ctx.chart.ctx;
                const gradient = canvas.createLinearGradient(1000, 0, 0, 40);
                gradient.addColorStop(0, 'rgba(255, 130, 0, 0.4)');
                gradient.addColorStop(0.5, 'rgba(255, 130, 0, 0.35)');
                gradient.addColorStop(1, 'rgba(255, 130, 0, 0.2)');

                return gradient;
              },
              borderColor: '#ffffee',
              pointBackgroundColor: '#990000',
              borderWidth: 1,
              pointBorderColor: 'white',
              tension: 0.25,
            }],
          }"
          :options="{
            showLine: true,
            // responsive: true,
            parsing: {
              xAxisKey: 'date',
              yAxisKey: 'rate'   
            },
          }"
        />
      </div>
      <!-- <pre>{{ data }}</pre> -->
    </div>
  </div>
</template>

<style scoped>
chart {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 800px;
}

h1 {
  font-weight: 500;
  font-size: 2.6rem;
  position: relative;
  top: -10px;
}

h3 {
  font-size: 1.2rem;
}

.greetings h1,
.greetings h3 {
  text-align: center;
}

@media (min-width: 1024px) {

  .greetings h1,
  .greetings h3 {
    text-align: left;
  }
}
</style>
