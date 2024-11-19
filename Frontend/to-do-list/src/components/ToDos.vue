<template>
  <div>
    <the-nav></the-nav>
    <div class="container">
      <div class="task-input-container">
        <h5><b>Task Description:</b></h5>
        <input
          class="small-input"
          v-model="description"
          placeholder="Enter task description"
          style="width: 200px"
        />
        <input class="small-input" type="date" v-model="dueDate" />
        <button @click="addNewTask" class="task-action-button status-button">
          Add Task
        </button>
      </div>
      <div class="search-container">
        <input
          type="text"
          v-model="description"
          @input="searchTask"
          placeholder="Search by task description"
          style="
            height: 40px;
            width: 200px;
            border-radius: 20px;
            border: 1px solid #ccc;
            padding: 0 10px;
          "
        />
        &nbsp;
        <select
          id="status-select"
          v-model="filterStatus"
          @change="getTaskByStatus"
          style="
            height: 40px;
            border-radius: 20px;
            border: 1px solid #ccc;
            padding: 0 10px;
            margin-left: 10px;
          "
        >
          <option value="all">All</option>
          <option value="uncompleted">To-Do</option>
          <option value="inprogress">In-progress</option>
          <option value="completed">Completed</option>
        </select>
        &nbsp;
        <div class="date-input-container">
          <input
            type="date"
            v-model="dueDate"
            @change="filterByDueDate"
            style="
              height: 40px;
              width: 150px;
              font-size: 16px;
              border-radius: 20px;
              border: 1px solid #ccc;
              padding: 0 10px;
            "
          />
          <i
            v-if="dueDate"
            class="bi bi-x"
            @click="clearDueDate"
            style="cursor: pointer; font-size: 30px; color: red"
          ></i>
        </div>
      </div>
      <div v-if="isLoading">
        <base-spinner />
      </div>
      <table class="task-table">
        <thead>
          <tr>
            <th>
              Description &nbsp;
              <i
                class="bi bi-sort-down"
                @click="sortByDescription"
                style="font-size: 20px"
              ></i>
              <i
                v-if="filterApplied"
                class="bi bi-x"
                @click="clearFilters"
                style="font-size: 25px; color: red; cursor: pointer"
              ></i>
            </th>
            <th>
              Due Date &nbsp;
              <i
                class="bi bi-sort-down"
                @click="sortByDueDate"
                style="font-size: 20px"
              ></i>
              <i
                v-if="filterApplied"
                class="bi bi-x"
                @click="clearFilters"
                style="font-size: 25px; color: red; cursor: pointer"
              ></i>
            </th>
            <th>Progress</th>
            <th>Status</th>
            <th>Actions</th>
            <th>File</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="task in tasks" :key="task.id">
            <td>{{ task.description }}</td>
            <td>{{ task.dueDate }}</td>
            <td>
              <p :style="{ textAlign: 'center' }">{{ task.progress }}%</p>
              <div class="progressbar">
                <div
                  class="progressbar__value"
                  :style="{ width: task.progress + '%' }"
                ></div>
              </div>
            </td>
            <td>
              <div class="icon-container">
                <template v-if="task.progress === 100">
                  <i
                    class="bi bi-check-square-fill"
                    style="font-size: 20px; color: green"
                  ></i>
                  <span class="tooltip">Completed</span>
                </template>

                <template v-else-if="task.progress === 0">
                  <i
                    class="bi bi-clock"
                    style="font-size: 20px; color: red"
                  ></i>
                  <span class="tooltip">Not Started</span>
                </template>

                <template v-else-if="task.progress >= 1 && task.progress <= 99">
                  <i
                    class="bi bi-hourglass-split"
                    style="font-size: 20px; color: #17a2b8"
                  ></i>
                  <span class="tooltip">In Progress</span>
                </template>

                <template v-else>
                  <i
                    class="bi bi-backspace-fill"
                    style="font-size: 20px; color: #777"
                  ></i>
                  <span class="tooltip">Unknown</span>
                </template>
              </div>
            </td>

            <td class="task-actions">
              <div class="icon-container">
                <i
                  class="bi bi-pencil-fill tooltip-icon"
                  style="font-size: 20px"
                  @click="startEdit(task)"
                ></i>
                <span class="tooltip">Edit</span>
              </div>
              &nbsp;
              <div class="icon-container">
                <i
                  class="bi bi-trash3-fill tooltip-icon"
                  style="font-size: 20px"
                  @click="deleteTask(task.id)"
                ></i>
                <span class="tooltip">Delete</span>
              </div>
              &nbsp;

              <div class="icon-container">
                <input
                  type="file"
                  class="btn btn-dark"
                  @change="onFileChange"
                  style="display: none; font-size: 20px"
                  id="fileInput"
                />
                <label for="fileInput" style="cursor: pointer">
                  <i class="bi bi-upload" style="font-size: 20px"></i>
                </label>
                <span class="tooltip">Upload</span>
              </div>

              &nbsp;

              <button
                class="btn btn-dark"
                @click="uploadFile(task.id)"
                
                >
                Upload
              </button>
            </td>
            <td>
              <div class="icon-container">
                <i
                  class="bi bi-download"
                  type="button"
                  v-if="task.id"
                  style="font-size: 20px"
                  @click="downloadFile(task.id)"
                ></i>
                <span class="tooltip">Download</span>
              </div>
              &nbsp;

              <div class="popup" v-if="showPopup">
                <div class="popup-content">
                  <div class="card-header">
                    <h5 class="modal-title">File Preview</h5>
                    <button
                      class="close close-button"
                      @click="showPopup = false"
                    >
                      &times;
                    </button>
                  </div>
                  <div class="card-body">
                    <p>{{ filePreview }}</p>
                  </div>
                </div>
              </div>

              <div class="icon-container">
                <button
                  type="button"
                  class="btn btn-dark"
                  @click="previewFile(task.id)"
                  :disabled="!task.filePath"
                  style="position: relative"
                >
                  Preview
                </button>
                <span
                  class="tooltip"
                  v-if="!task.filePath"
                  style="
                    position: absolute;
                    bottom: 120%;
                    left: 50%;
                    transform: translateX(-50%);
                  "
                >
                  File not available
                </span>
              </div>
            </td>
          </tr>
          <tr v-if="tasks.length === 0">
            <td colspan="6" style="text-align: center">No tasks available</td>
          </tr>
        </tbody>
      </table>

      <div class="pagination-controls">
      <i
        class="bi bi-arrow-left-circle"
        @click="prevPage"
        style="font-size: 20px"
      ></i>
      &nbsp;
      <span>Page {{ page + 1 }} of {{ totalPages }}</span>
      &nbsp;
      <i
        class="bi bi-arrow-right-circle"
        @click="nextPage"
        style="font-size: 20px"
      ></i>
    </div>

      <div v-if="isEditing" class="modal">
        <div class="modal-content">
          <span class="close-button" @click="cancelEdit">&times;</span>
          <h2>Edit Task</h2>
          <label>
            Title:
            <input
              v-model="editTask.description"
              placeholder="Task Description"
            />
          </label>
          <br />
          <label>
            Due date:
            <input type="date" v-model="editTask.dueDate" />
          </label>
          <br />
          <label>
            Progress:
            <input
              type="number"
              v-model="editTask.progress"
              placeholder="Progress (%)"
            />
          </label>
          <br />
          <label>
            Status:
            <input type="checkbox" v-model="editTask.status" />
          </label>
          <button @click="updateTask(editTask.id)">Save</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/utils/axios";
import store from "@/store";
import TheNav from "./TheNav.vue";
import { useToast } from "vue-toastification";
import BaseSpinner from "./BaseSpinner.vue";

export default {
  components: { TheNav, BaseSpinner },
  data() {
    return {
      tasks: [],
      description: "",
      dueDate: "",
      search: "",
      page: 0,
      filePath: "",
      taskLimit: 5,
      userId: store.state.userId,
      isEditing: false,
      isLoading: false,
      editTask: {
        id: null,
        description: "",
        status: false,
        dueDate: "",
        progress: 0,
      },
      totalTasks: 0,
      filterStatus: "all",
      filterApplied: false,
      showPopup: false,
      filePreview: "",
      selectedFile: null,
    };
  },
  computed: {
    paginatedTasks() {
      const start = this.page * this.taskLimit;
      return this.filteredTasks.slice(start, start + this.taskLimit);
    },
    totalPages() {     
      return Math.ceil(this.totalTasks / this.taskLimit);
    },
    filteredTasks() {
      return this.tasks.filter((task) =>
        task.description.toLowerCase().includes(this.search.toLowerCase())
      );
    },
  },
  mounted() {
    this.getTasksByUserId();
  },
  methods: {
    prevPage() {
      if (this.page > 0) {
        this.page--;
        this.getTasksByUserId();
      }
    },
    nextPage() {
      if (this.page < this.totalPages - 1) {
        this.page++;
        console.log("Page No: "+this.page);
        this.getTasksByUserId();
      }
    },
    onFileChange(event) {
      this.selectedFile = event.target.files[0];

      if (this.selectedFile) {
        if (this.selectedFile.type === "application/pdf") {
          this.filePreview = this.selectedFile.name;
        } else {
          alert("Please select a valid PDF file.");
          this.selectedFile = null;
          this.filePreview = "";
        }
      }
    },
    async deleteTask(taskId)
    {
      try
      {
        api.delete(`http://localhost:9090/task/${this.userId}/${taskId}`)
        const toast = useToast();
        toast.success("Deleted task successfully");
        this.refreshTasks();
      }
      catch(error)
      {
        console.log(error);        
        const toast = useToast();
        toast.error("Error deleting file");
      }
    },
    async searchTask()
    {
        try
        {
          const response = await api.get(`http://localhost:9090/tasks/search/${this.userId}`,
            {
              params:
              {
                taskName: this.description
              }
            }
          )
          this.tasks = response.data;
        }
        catch(error)
        {
          console.log(error);
          
        }
    },
    async uploadFile(taskId) {
      if (!this.selectedFile || !taskId) {
        const toast = useToast();
        toast.error("Please select file first");
        return;
      }

      const formData = new FormData();
      formData.append("file", this.selectedFile);
      console.log([...formData]);

      try {
        this.isLoading = true;
        const response = await api.post(
          `http://localhost:9090/${taskId}/upload`,
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        this.refreshTasks();
        const toast = useToast();
        toast.success("File uploaded successfully!");
        console.log(response);
      } catch (error) {
        console.error("Error uploading file:", error);
        const toast = useToast();
        toast.error("Please select file first");
      } finally {
        this.isLoading = false;
      }
    },
    async previewFile(taskId) {
      try {
        const response = await api.get(
          `http://localhost:9090/task/${taskId}/preview`
        );
        this.filePreview = response.data;
        this.showPopup = true;
        console.log(this.filePreview);
      } catch (error) {
        console.error("Error fetching file preview:", error);
        const toast = useToast();
        toast.error("No file available");
        console.error(error);
      }
    },
    closePopup() {
      this.showPopup = false;
      this.filePreview = "";
    },
  async getTasksByUserId() {            
    this.isLoading = true;
  try {
    const response = await api.get(
      `http://localhost:9090/tasks/${this.userId}`,
      {
        params: { page: this.page, size: this.taskLimit },
      }
    );
    console.log("Page count : "+this.page);
    
    console.log(response);    
    this.tasks = response.data.tasks || [];
    
    this.totalTasks = response.data.totalTasks || 0; 
    this.totalPages = Math.ceil(this.totalTasks / this.taskLimit);
    
    console.log("Fetched tasks:", this.tasks);
    console.log("Total tasks:", this.totalTasks);
    console.log("Total pages:", this.totalPages);
  } catch (error) {
    console.error("Error fetching tasks", error);
  } finally {
    this.isLoading = false;
  }
},
    refreshTasks() {
      this.page = 0;
      this.dueDate = "";
      this.search = "";
      this.filterStatus = "all"; // Reset filter status
      this.filterApplied = false; // Reset filter applied status
      this.getTasksByUserId();
    },
    async addNewTask() {
      // Validate input fields
      if (!this.description.trim()) {
        const toast = useToast();
        toast.error("Task description cannot be empty.");
        return;
      }
      if (!this.dueDate) {
        const toast = useToast();
        toast.error("Task due date cannot be empty.");
        return;
      }

      try {
        // Create a FormData object to hold the task details
        const formData = new FormData();
        formData.append("description", this.description);
        formData.append("dueDate", this.dueDate);
        formData.append("progress", 0); // Initial progress
        formData.append("status", false); // Initial status

        // Send a POST request with the FormData
        const response = await api.post(
          `http://localhost:9090/task/${this.userId}`,
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data", // Important for FormData
            },
          }
        );

        // Log the response for debugging
        console.log(response);

        // Clear the input fields after successful task addition
        this.description = "";
        this.dueDate = null;
        this.refreshTasks(); // Refresh the task list

        // Show success toast
        const toast = useToast();
        toast.success("New Task Added!");
      } catch (error) {
        // Handle errors and show an error toast
        const toast = useToast();
        toast.error("Error adding new task. Please try again.");
        console.error(error); // Log the error for debugging
      }
    },
    async updateTask(taskId) {
      if (!this.editTask.description.trim()) {
        const toast = useToast();
        toast.error("Task cannot be empty");
        return;
      }
      try {
        await api.put(`http://localhost:9090/${taskId}`, {
          description: this.editTask.description,
          dueDate: this.editTask.dueDate,
          progress: this.editTask.progress,
          status: this.editTask.status,
        });
        this.isEditing = false;
        this.editTask = {
          id: null,
          description: "",
          status: false,
          dueDate: "",
          progress: 0,
        };
        this.refreshTasks();
        const toast = useToast();
        toast.success("Task updated!");
      } catch (error) {
        const toast = useToast();
        toast.error("Error updating task");
      }
    },
    startEdit(task) {
      this.editTask = { ...task };
      this.isEditing = true;
    },
    cancelEdit() {
      this.isEditing = false;
      this.editTask = {
        id: null,
        description: "",
        status: false,
        dueDate: "",
        progress: 0,
      };
    },
    async sortByDueDate() {
      try {
        const response = await api.get(
          `http://localhost:9090/sorted/${this.userId}`,
          {
            params: {
              userId: this.userId,
              dueDate: this.dueDate,
            },
          }
        );
        this.tasks = response.data;
        this.totalTasks = this.tasks.length;
        this.filterApplied = true; // Mark that filter is applied
      } catch (error) {
        console.error("Error filtering tasks", error);
      }
    },
    async sortByDescription() {
      try {
        const response = await api.get(
          `http://localhost:9090/sortedbydescription/${this.userId}`,
          {
            params: {
              userId: this.userId,
            },
          }
        );
        this.tasks = response.data;
        this.totalTasks = this.tasks.length;
        console.log(this.totalPages);        
        console.log(this.totalTasks);
        
        this.filterApplied = true; // Mark that filter is applied
      } catch (error) {
        console.error("Error filtering tasks", error);
      }
    },
    async filterByDueDate() {
      if (!this.dueDate) {
        const toast = useToast();
        toast.error("Please select a due date.");
        return;
      }
      this.page = 0;
      try {
        const response = await api.get("http://localhost:9090/tasks/due-date", {
          params: {
            userId: this.userId,
            dueDate: this.dueDate,
          },
        });
        this.tasks = response.data;
        this.totalTasks = this.tasks.length;
        this.filterApplied = true; // Mark that filter is applied
      } catch (error) {
        console.error("Error filtering tasks", error);
      }
    },
    async getTaskByStatus() {
      try {
        this.isLoading = true;
        let endpoint = `http://localhost:9090/tasks`;
        const status = this.filterStatus === "all" ? "" : this.filterStatus;
        if (status) {
          endpoint += `/${status}/${this.userId}`;
        } else {
          this.getTasksByUserId();
          return;
        }
        const response = await api.get(endpoint, {
          params: { page: this.page, size: this.taskLimit },
        });
        this.tasks = response.data || [];
        this.totalTasks = this.tasks.length;
        this.filterApplied = true; // Mark that filter is applied
      } catch (error) {
        console.error("Error fetching tasks by status", error);
      } finally {
        this.isLoading = false;
      }
    },
    async downloadFile(id) {
      try {
        const response = await api.get(
          `http://localhost:9090/downloadfile/${id}`,
          {
            responseType: "blob",
          }
        );

        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", `resume_${id}.pdf`);
        document.body.appendChild(link);
        link.click();
      } catch (error) {
        console.error("Error downloading resume", error);
      }
    },
    clearDueDate() {
      this.dueDate = "";
      this.refreshTasks();
    },
    clearFilters() {
      this.refreshTasks();
    },
  },
};
</script>

<style scoped>
.container {
  position: relative;
}

.task-input-container {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 20px 0;
}

.task-input-container input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 20px;
  width: 150px;
}

i {
  cursor: pointer;
}

.search-container {
  display: flex;
  justify-content: left;
  margin: 20px 0;
}

.search-container input {
  padding: 12px 16px;
  font-size: 14px;
  border: 2px solid #ddd;
  border-radius: 30px;
}

.button-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.button-group {
  display: flex;
  gap: 10px;
}

.task-action-button {
  padding: 10px 15px;
  border: none;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  width: 120px;
}

.status-button {
  background-color: #f34c5d;
}

.task-table {
  width: 100%;
  border-collapse: collapse;
}

.task-table th,
.task-table td {
  padding: 12px;
  text-align: left;
  border: 1px solid black;
}

.progressbar {
  width: 100%;
  background-color: #f3f3f3;
}

.progressbar__value {
  height: 20px;
  background-color: #4caf50;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 5px;
  width: 400px;
}

.close-button {
  cursor: pointer;
  float: right;
}

.date-input-container {
  display: flex;
  align-items: center;
}

.icon-container {
  position: relative;
  display: inline-block;
}

.icon-container {
  position: relative;
  display: inline-block;
}

.tooltip {
  visibility: hidden;
  width: max-width;
  background-color: grey;
  color: #fff;
  text-align: center;
  border-radius: 5px;
  padding: 5px;
  position: absolute;
  z-index: 1;
  bottom: 125%;
  left: 50%;
  margin-left: -30px;
  opacity: 0;
  transition: opacity 0.3s;
}

.icon-container:hover .tooltip {
  visibility: visible;
  opacity: 1;
}

.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: none;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.preview-content {
  background: white;
  padding: 20px;
  border-radius: 5px;
  width: 80%;
  max-width: 600px;
}
.close-button {
  cursor: pointer;
  float: right;
}
.popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8); /* Dark overlay */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  width: 50%; /* Set to 90% of the viewport width */
  max-width: 1000px; /* Increased max width for a larger card */
  border-radius: 0.5rem; /* Smooth corners */
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.3); /* Soft shadow */
  background-color: white;
  overflow: hidden;
  animation: fadeIn 0.3s ease; /* Smooth fade-in effect */
}

.card-header {
  background-color: #007bff; /* Header color */
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem; /* Adequate padding */
}

.card-header .close {
  cursor: pointer;
  font-size: 1.5rem;
  color: white;
  border: none;
  background: none;
  transition: color 0.3s; /* Smooth color transition */
}

.card-header .close:hover {
  color: #ff6b6b; /* Change color on hover */
}

.card-body {
  padding: 1.5rem; /* Comfortable padding */
  max-height: 300px; /* Reduced max height for a shorter card */
  overflow-y: auto; /* Allow vertical scrolling */
  color: #333; /* Dark text for readability */
  line-height: 1.5; /* Improved line height */
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95); /* Subtle scaling for effect */
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
