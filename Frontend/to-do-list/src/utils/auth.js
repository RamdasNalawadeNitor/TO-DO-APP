import { jwtDecode } from 'jwt-decode';  

export function userId(token){
  if (!token) return null;
  const decoded = jwtDecode(token);
  return decoded.userId;
}

// export function getEmpName(token){
//   if (!token) return null;
//   const decoded = jwtDecode(token);
//   return decoded.EmpName;
// }
 
export function isAuthenticated() {
  const token = localStorage.getItem('token');
  return !!token;
}