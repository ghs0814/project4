import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import axios from "axios";

// 고객
interface Customer {
  customerId: number;
  name: string;
  email: string;
}

// 상품
interface Product {
  productId: number;
  name: string;
  price: number;
  stockQuantity: number;
}

// 주문
interface Order {
  orderId: number;
  customerName: string;
  totalAmount: number;
  status: string;
}

// 주문 페이징
interface OrderPage {
  content: Order[];
  page: number;
  totalElements: number;
  totalPages: number;
}

// 검색 조건
interface OrderSearch {
  customerId?: number;
  status?: string;
}

// 주문 생성 응답
interface OrderResponse {
  orderId: number;
  totalAmount: number;
}

// 전체 매출
interface SalesSummary {
  orderCount: number;
  totalSales: number;
}

// 상품별 매출
interface ProductSales {
  productId: number;
  productName: string;
  totalQuantity: number;
  totalSales: number;
}

// 주문 목록 API
function getOrders(search: OrderSearch, page: number) {
  return axios.get<OrderPage>("/api/orders", {
    params: {
      ...search,
      page,
      size: 5,
      sort: "newest",
    },
  });
}

function App() {
  // 고객·상품
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [products, setProducts] = useState<Product[]>([]);

  // 주문 생성
  const [customerId, setCustomerId] = useState("");
  const [productId, setProductId] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [submitting, setSubmitting] = useState(false);

  // 주문 검색·페이징
  const [orders, setOrders] = useState<OrderPage | null>(null);
  const [search, setSearch] = useState<OrderSearch>({});
  const [page, setPage] = useState(1);
  const [cancelingId, setCancelingId] = useState<number | null>(null);

  // 매출
  const [summary, setSummary] = useState<SalesSummary | null>(null);
  const [productSales, setProductSales] = useState<ProductSales[]>([]);

  // 메시지
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [listError, setListError] = useState("");

  // 최초 고객·상품·매출 조회
  useEffect(() => {
    Promise.all([
      axios.get<Customer[]>("/api/customers"),
      axios.get<Product[]>("/api/products"),
      axios.get<SalesSummary>("/api/sales/summary"),
      axios.get<ProductSales[]>("/api/sales/products"),
    ])
      .then(([c, p, s, ps]) => {
        setCustomers(c.data);
        setProducts(p.data);
        setSummary(s.data);
        setProductSales(ps.data);
      })
      .catch(() => setError("초기 데이터 조회 실패"));
  }, []);

  // 검색 조건 또는 페이지가 변경되면 주문 조회
  useEffect(() => {
    let active = true;

    getOrders(search, page)
      .then((res) => {
        if (active) {
          setOrders(res.data);
          setListError("");
        }
      })
      .catch(() => {
        if (active) setListError("주문 목록 조회 실패");
      });

    return () => {
      active = false;
    }; // 클린업 함수 : 새로운 이 useEffect가 실행되기 전에 먼저 실행 또는 언마운트시 실행 -> 경쟁 방지
    //이전 요청의 응답이 늦게 도착했을 때 화면 덮어쓰기 방지
    // 새로운 useEffect실행시 이전 useEffect의 active를 false로 바꿔서, 로직에 active가 false이면 실행되지 않도록 작성
  }, [search, page]);

  // 주문·재고·매출 다시 조회
  async function refreshData() {
    try {
      const [p, o, s, ps] = await Promise.all([
        axios.get<Product[]>("/api/products"),
        getOrders(search, page),
        axios.get<SalesSummary>("/api/sales/summary"),
        axios.get<ProductSales[]>("/api/sales/products"),
      ]);

      setProducts(p.data);
      setOrders(o.data);
      setSummary(s.data);
      setProductSales(ps.data);
      setError("");
      setListError("");
    } catch {
      setError("데이터 갱신 실패. 새로고침해 주세요.");
    }
  }

  // 주문 생성
  async function handleOrder(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();

    setSubmitting(true);
    setMessage("");

    try {
      const res = await axios.post<OrderResponse>("/api/orders", {
        customerId: Number(customerId),
        items: [
          {
            productId: Number(productId),
            quantity,
          },
        ],
      });

      setMessage(`주문 ${res.data.orderId}번 생성 완료`);

      setCustomerId("");
      setProductId("");
      setQuantity(1);

      await refreshData();
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 409) {
        setMessage("재고가 부족합니다.");
      } else {
        setMessage("주문 생성 실패");
      }
    } finally {
      setSubmitting(false);
    }
  }

  // 주문 검색
  function handleSearch(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();

    const form = new FormData(e.currentTarget);

    const customer = String(form.get("customerId") ?? "");
    const status = String(form.get("status") ?? "");

    setSearch({
      customerId: customer ? Number(customer) : undefined,
      status: status || undefined,
    });

    setPage(1);
  }

  // 주문 취소
  async function handleCancel(orderId: number) {
    setCancelingId(orderId);
    setMessage("");

    try {
      await axios.post(`/api/orders/${orderId}/cancel`);

      setMessage(`주문 ${orderId}번 취소 완료`);

      await refreshData();
    } catch {
      setMessage("주문 취소 실패");
    } finally {
      setCancelingId(null);
    }
  }

  return (
    <div>
      <h1>Project4 주문·매출 관리 시스템</h1>

      {error && <p>{error}</p>}
      {message && <p>{message}</p>}

      {/* 고객 목록 */}
      <h2>고객 목록</h2>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>이름</th>
            <th>이메일</th>
          </tr>
        </thead>

        <tbody>
          {customers.map((customer) => (
            <tr key={customer.customerId}>
              <td>{customer.customerId}</td>
              <td>{customer.name}</td>
              <td>{customer.email}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <hr />

      {/* 상품 목록 */}
      <h2>상품 목록</h2>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>상품명</th>
            <th>가격</th>
            <th>재고</th>
          </tr>
        </thead>

        <tbody>
          {products.map((product) => (
            <tr key={product.productId}>
              <td>{product.productId}</td>
              <td>{product.name}</td>
              <td>{product.price.toLocaleString()}원</td>
              <td>{product.stockQuantity}개</td>
            </tr>
          ))}
        </tbody>
      </table>

      <hr />

      {/* 주문 생성 */}
      <h2>주문 생성</h2>

      <form onSubmit={handleOrder}>
        <p>
          고객:
          <select
            value={customerId}
            onChange={(e) => setCustomerId(e.target.value)}
            required
          >
            <option value="">고객 선택</option>

            {customers.map((customer) => (
              <option key={customer.customerId} value={customer.customerId}>
                {customer.name}
              </option>
            ))}
          </select>
        </p>

        <p>
          상품:
          <select
            value={productId}
            onChange={(e) => setProductId(e.target.value)}
            required
          >
            <option value="">상품 선택</option>

            {products.map((product) => (
              <option key={product.productId} value={product.productId}>
                {product.name}
              </option>
            ))}
          </select>
        </p>

        <p>
          수량:
          <input
            type="number"
            min="1"
            step="1"
            value={quantity}
            onChange={(e) => setQuantity(Number(e.target.value))}
            required
          />
        </p>

        <button type="submit" disabled={submitting}>
          {submitting ? "주문 중..." : "주문하기"}
        </button>
      </form>

      <hr />

      {/* 주문 검색 */}
      <h2>주문 목록</h2>

      <form onSubmit={handleSearch}>
        <p>
          고객:
          <select name="customerId">
            <option value="">전체</option>

            {customers.map((customer) => (
              <option key={customer.customerId} value={customer.customerId}>
                {customer.name}
              </option>
            ))}
          </select>
        </p>

        <p>
          상태:
          <select name="status">
            <option value="">전체</option>
            <option value="ORDERED">주문 완료</option>
            <option value="CANCELED">취소</option>
          </select>
        </p>

        <button type="submit">검색</button>
      </form>

      {listError && <p>{listError}</p>}

      {orders && (
        <>
          <p>전체 {orders.totalElements}건</p>

          <table>
            <thead>
              <tr>
                <th>주문번호</th>
                <th>고객</th>
                <th>금액</th>
                <th>상태</th>
                <th>취소</th>
              </tr>
            </thead>

            <tbody>
              {orders.content.map((order) => (
                <tr key={order.orderId}>
                  <td>{order.orderId}</td>
                  <td>{order.customerName}</td>
                  <td>{order.totalAmount.toLocaleString()}원</td>
                  <td>{order.status}</td>
                  <td>
                    {order.status === "ORDERED" ? (
                      <button
                        type="button"
                        disabled={cancelingId !== null}
                        onClick={() => handleCancel(order.orderId)}
                      >
                        {cancelingId === order.orderId
                          ? "취소 중..."
                          : "주문 취소"}
                      </button>
                    ) : (
                      "-"
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {orders.content.length === 0 && <p>조회된 주문이 없습니다.</p>}

          {/* 페이징 */}
          <p>
            <button
              type="button"
              disabled={page <= 1}
              onClick={() => setPage(page - 1)}
            >
              이전
            </button>{" "}
            {orders.totalPages === 0 ? 0 : orders.page}
            {" / "}
            {orders.totalPages}{" "}
            <button
              type="button"
              disabled={page >= orders.totalPages}
              onClick={() => setPage(page + 1)}
            >
              다음
            </button>
          </p>
        </>
      )}

      <hr />

      {/* 전체 매출 */}
      <h2>전체 매출</h2>

      {summary && (
        <div>
          <p>주문 건수: {summary.orderCount}건</p>
          <p>총매출: {summary.totalSales.toLocaleString()}원</p>
        </div>
      )}

      <hr />

      {/* 상품별 매출 */}
      <h2>상품별 매출</h2>

      <table>
        <thead>
          <tr>
            <th>상품명</th>
            <th>판매 수량</th>
            <th>매출</th>
          </tr>
        </thead>

        <tbody>
          {productSales.map((sale) => (
            <tr key={sale.productId}>
              <td>{sale.productName}</td>
              <td>{sale.totalQuantity}개</td>
              <td>{sale.totalSales.toLocaleString()}원</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
