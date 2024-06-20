export default interface JwtInterface {
    id: number;
    email: string;
    roles: string;
    changed: boolean;
    iat: number;
    exp: number;
}